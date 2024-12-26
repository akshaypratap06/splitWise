package com.example.splitwise.manager;

import com.example.splitwise.ExpenseType;
import com.example.splitwise.dao.ExpenseDao;
import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.entity.ExpenseEntity;
import com.example.splitwise.entity.ExpenseRecordEntity;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.entity.UserTransactionEntity;
import com.example.splitwise.model.Expense;
import com.example.splitwise.model.ExpenseAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseManager {
    @Autowired
    protected ExpenseDao expenseDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected GroupDao groupDao;

    public ExpenseRecordEntity createExpense(Expense expense) throws Exception {
        UUID id= UUID.randomUUID();
        if (expense.getPaidByType().equals(ExpenseType.PERCENTAGE)) {
            updateExpenseByPercentage(expense.getPaidBy(), expense.getPaidAmount());
        }
        if (expense.getPaidForType().equals(ExpenseType.PERCENTAGE)) {
            updateExpenseByPercentage(expense.getPaidFor(), expense.getPaidAmount());
        } else if (expense.getPaidForType().equals(ExpenseType.EQUAL)) {
            updateExpensePaidForEqual(expense.getPaidFor(), expense.getPaidAmount());
        }
        validateCount(expense);
        Set<String> users = findAllUsers(expense);
        validateRequest(users, expense.getGroupId());
        expenseSplitter(expense, users);
        expenseDao.saveUserTransaction(id,users);
        return expenseDao.saveRecord(expense,id);
    }

    private Set<String> findAllUsers(Expense expense) {
        Set<String> users = new HashSet<>();
        users.addAll(expense.getPaidFor().keySet());
        users.addAll(expense.getPaidBy().keySet());
        return users;
    }

    protected void validateCount(Expense expense) throws Exception {
        double countPaidBy = expense.getPaidBy().values().stream().mapToDouble(p -> Double.parseDouble(Float.toString(p))).sum();
        double countPaidFor = expense.getPaidFor().values().stream().mapToDouble(p -> Double.parseDouble(Float.toString(p))).sum();
        if (expense.getPaidAmount() - countPaidBy > 0 || expense.getPaidAmount() - countPaidFor > 0 || expense.getPaidAmount() - countPaidBy < 0 || expense.getPaidAmount() - countPaidFor < 0) {
            throw new Exception("Wrong paid amount" + expense.getPaidAmount() + " " + countPaidBy + " " + countPaidFor);
        }
    }

    protected void updateExpensePaidForEqual(Map<String, Float> expense, Float paidAmount) {
        float remainingPaidAmount = paidAmount;
        float share = Float.parseFloat(String.format("%.2f", paidAmount / expense.size()));
        int size = expense.size();
        int count = 0;
        for (Map.Entry<String, Float> ex : expense.entrySet()) {
            count++;
            if (count != size) {
                ex.setValue(share);
                remainingPaidAmount -= share;
            } else {
                ex.setValue(Float.parseFloat(String.format("%.2f", remainingPaidAmount)));
            }
        }
    }

    protected void expenseSplitter(Expense expense, Set<String> allUser) {
        expenseCreator(expense, allUser);
    }

    private void expenseCreator(Expense expense, Set<String> allUser) {

        Map<String, Float> giverUser = new HashMap<>();
        Map<String, Float> takerUser = new HashMap<>();
        //in this particular transaction you are giving or taking amount and how much string is user and float is amount
        updateGiverAndTakerMap(expense, allUser, takerUser, giverUser);
        //string is user  ie. akshay
        //map.string is also user ie. akshay
        //ExpenseEntity is Entity for the expense remove own user at end
        Map<String, Map<String, ExpenseEntity>> expenseEntityMap = generateExpenseEntityMap(expense, allUser);

        List<ExpenseEntity> expenseEntities = expenseDao.getExpenses(allUser, expense.getGroupId());
        expenseEntities.forEach(p -> expenseEntityMap.get(p.getUserId()).put(p.getOtherUserId(), p));

        Queue<ExpenseAmount> queueOfGiver = convertToExpenseQueue(giverUser, true);
        Queue<ExpenseAmount> queueOfTaker = convertToExpenseQueue(takerUser, false);

        expenseEntityUpdater(queueOfGiver, queueOfTaker, expenseEntityMap);
        List<ExpenseEntity> expenseEntityList = getAllExpenseEntity(expenseEntityMap);
        expenseDao.updateAllExpense(expenseEntityList);
    }

    private void expenseEntityUpdater(Queue<ExpenseAmount> queueOfGiver, Queue<ExpenseAmount> queueOfTaker, Map<String, Map<String, ExpenseEntity>> expenseEntityMap) {
        while (!queueOfGiver.isEmpty() && !queueOfTaker.isEmpty()) {
            ExpenseAmount giverObj = queueOfGiver.peek();
            ExpenseAmount takerObj = queueOfTaker.peek();
            if (Math.abs(giverObj.getAmount()) == Math.abs(takerObj.getAmount())) {
                expenseEntityMap.get(giverObj.getUser()).get(takerObj.getUser()).setLentAmount(expenseEntityMap.get(giverObj.getUser()).get(takerObj.getUser()).getLentAmount() + Math.abs(takerObj.getAmount()));
                expenseEntityMap.get(takerObj.getUser()).get(giverObj.getUser()).setLentAmount(expenseEntityMap.get(takerObj.getUser()).get(giverObj.getUser()).getLentAmount() - Math.abs(takerObj.getAmount()));
                queueOfGiver.poll();
                queueOfTaker.poll();
            } else if (Math.abs(takerObj.getAmount()) < Math.abs(giverObj.getAmount())) {
                expenseEntityMap.get(giverObj.getUser()).get(takerObj.getUser()).setLentAmount(expenseEntityMap.get(giverObj.getUser()).get(takerObj.getUser()).getLentAmount() + Math.abs(takerObj.getAmount()));
                expenseEntityMap.get(takerObj.getUser()).get(giverObj.getUser()).setLentAmount(expenseEntityMap.get(takerObj.getUser()).get(giverObj.getUser()).getLentAmount() - Math.abs(takerObj.getAmount()));
                queueOfTaker.poll();
                giverObj.setAmount(giverObj.getAmount() - Math.abs(takerObj.getAmount()));
            } else {
                expenseEntityMap.get(giverObj.getUser()).get(takerObj.getUser()).setLentAmount(expenseEntityMap.get(giverObj.getUser()).get(takerObj.getUser()).getLentAmount() + Math.abs(giverObj.getAmount()));
                expenseEntityMap.get(takerObj.getUser()).get(giverObj.getUser()).setLentAmount(expenseEntityMap.get(takerObj.getUser()).get(giverObj.getUser()).getLentAmount() - Math.abs(giverObj.getAmount()));
                queueOfGiver.poll();
                takerObj.setAmount(takerObj.getAmount() + giverObj.getAmount());
            }
        }
    }

    private void updateGiverAndTakerMap(Expense expense, Set<String> allUser, Map<String, Float> takerUser, Map<String, Float> giverUser) {
        allUser.forEach(p -> {
            float payBy = expense.getPaidBy().getOrDefault(p, 0.0f);
            float payFor = expense.getPaidFor().getOrDefault(p, 0.0f);
            float finalResult = payBy - payFor;
            if (finalResult < 0) {
                takerUser.put(p, finalResult);
            } else if (finalResult > 0) {
                giverUser.put(p, finalResult);
            }
        });
    }

    protected Map<String, Map<String, ExpenseEntity>> generateExpenseEntityMap(Expense expense, Set<String> users) {
        Map<String, Map<String, ExpenseEntity>> expenseEntityMap = new HashMap<>();
        //string is user  ie. akshay
        //map.string is also user ie. akshay
        //ExpenseEntity is Entity for the expense remove own user at end
        users.forEach(p -> {
            expenseEntityMap.put(p, new HashMap<>());
            Map<String, ExpenseEntity> expenseMap = expenseEntityMap.get(p);
            users.forEach(a -> expenseMap.put(a, new ExpenseEntity(p, a, expense.getGroupId(), 0)));
            expenseMap.remove(p);
        });
        return expenseEntityMap;
    }

    protected Queue<ExpenseAmount> convertToExpenseQueue(Map<String, Float> users, boolean reverse) {
        List<ExpenseAmount> expenseAmountList = new ArrayList<>();
        for (Map.Entry<String, Float> user :
                users.entrySet()) {
            expenseAmountList.add(new ExpenseAmount(user.getValue(), user.getKey()));
        }
        if (!reverse) {
            expenseAmountList.sort((o1, o2) -> Float.compare(o1.getAmount(), o2.getAmount()));
        } else {
            expenseAmountList.sort((o1, o2) -> -(Float.compare(o1.getAmount(), o2.getAmount())));
        }
        return new ArrayDeque<>(expenseAmountList);
    }

    protected void validateRequest(Set<String> users, String groupId) throws Exception {
        List<UserEntity> userList = userDao.getUsers(users);
        if (userList.size() != users.size()) {
            throw new Exception("Unknown user detected");
        }
        groupDao.getGroup(groupId);

    }

    protected void updateExpenseByPercentage(Map<String, Float> expense, float paidAmount) {
        float remainingPaidAmount = paidAmount;
        int count = 0;
        int size = expense.entrySet().size();
        for (Map.Entry<String, Float> ex : expense.entrySet()) {
            count++;
            if (count != size) {
                float currPercentage = percentageCalculator(paidAmount, ex.getValue());
                ex.setValue(currPercentage);
                remainingPaidAmount -= currPercentage;
            } else {
                ex.setValue(Float.parseFloat(String.format("%.2f", remainingPaidAmount)));
            }
        }
    }

    protected Float percentageCalculator(Float paidAmount, Float value) {
        return Float.parseFloat(String.format("%.2f", ((paidAmount * value) / 100)));
    }

    public List<ExpenseEntity> getAllExpense() {
        return expenseDao.getAllExpense();
    }

    public void deleteExpense(String uuid) throws Exception {
        ExpenseRecordEntity expenseRecordEntity = expenseDao.getRecord(UUID.fromString(uuid));

        Set<String> allUser = new HashSet<>();
        allUser.addAll(getPaidUsers(expenseRecordEntity.getPaidFor()));
        allUser.addAll(getPaidUsers(expenseRecordEntity.getPaidBy()));

        Map<String, Float> paidBy = convertToPaid(expenseRecordEntity.getPaidBy());
        Map<String, Float> paidFor = convertToPaid(expenseRecordEntity.getPaidFor());

        Expense expense = new Expense();
        expense.setPaidBy(paidFor);
        expense.setPaidFor(paidBy);
        expense.setGroupId(expenseRecordEntity.getGroupId());
        expense.setPaidAmount(expenseRecordEntity.getPaidAmount());
        expenseCreator(expense, allUser);
        expenseDao.deleteRecord(UUID.fromString(uuid));
    }

    protected Set<String> getPaidUsers(String paidFor) {
        Set<String> user = new HashSet<>();
        String[] arr1 = paidFor.split(";");
        for (String arr :
                arr1) {
            String[] arr2 = arr.split("_");
            user.add(arr2[0]);
        }
        return user;
    }

    protected List<ExpenseEntity> getAllExpenseEntity(Map<String, Map<String, ExpenseEntity>> expenseEntityMap) {
        return expenseEntityMap.entrySet().stream().flatMap(p -> p.getValue().values().stream()).toList();
    }

    protected Map<String, Float> convertToPaid(String paid) {
        String[] paidByUser = paid.split(";");
        Map<String, Float> paidMap = new HashMap<>();
        for (String payer : paidByUser) {
            String[] paidByArray = payer.split("_");
            paidMap.put(paidByArray[0], Float.parseFloat(paidByArray[1]));
        }
        return paidMap;
    }

    public ExpenseRecordEntity updateThisExpense(String uuid, Expense expense) throws Exception {
        deleteExpense(uuid);
        return createExpense(expense);
    }

    public List<ExpenseRecordEntity> getAllExpenseRecord() {
        return expenseDao.getAllRecord();
    }
}
