package com.example.splitwise.manager;

import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.model.ExpenseAmount;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.entity.ExpenseEntity;
import com.example.splitwise.entity.ExpenseRecordEntity;
import com.example.splitwise.ExpenseType;
import com.example.splitwise.dao.ExpenseDao;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseManager {
    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    public ExpenseRecordEntity createExpense(Expense expense) throws Exception {
        validateRequest(expense);
        if (expense.getType().equals(ExpenseType.PERCENTAGE)) {
            updateExpense(expense);
        }
        dividev2(expense);
        return expenseDao.saveRecord(expense);
    }

    private void dividev2(Expense expense) {
        Set<String> users= new HashSet<>(expense.getPaidFor());
        users.addAll(expense.getPaidBy().keySet());
        Map<String,Map<String,ExpenseEntity>> expenseEntityMap= new HashMap<>();
        users.forEach(p->{
            expenseEntityMap.put(p,new HashMap<>());
            Map<String,ExpenseEntity> expenseMap= expenseEntityMap.get(p);
            users.forEach(a->expenseMap.put(a,new ExpenseEntity(p,a,expense.getGroupId(),0)));
            expenseMap.remove(p);
        });
        List<ExpenseEntity> expenseEntities= expenseDao.getExpenses(users,expense.getGroupId());
        expenseEntities.forEach(p->{
            expenseEntityMap.get(p.getUserId()).put(p.getOtherUserId(),p);
        });
        for(Map.Entry<String,Integer> user:expense.getPaidBy().entrySet()){
            double share= (double) user.getValue() /expense.getPaidFor().size();
            for(String payUser:expense.getPaidFor()){
                if(user.getKey().equals(payUser))
                    continue;
                expenseEntityMap.get(user.getKey()).get(payUser).setLentAmount(expenseEntityMap.get(user.getKey()).get(payUser).getLentAmount()+share);
                expenseEntityMap.get(payUser).get(user.getKey()).setLentAmount(expenseEntityMap.get(payUser).get(user.getKey()).getLentAmount()-share);
            }
        }
        List<ExpenseEntity> allEntity= getAllEntity(expenseEntityMap);

        expenseDao.updateAllExpense(allEntity);
    }

    private void validateRequest(Expense expense) throws Exception {
        Set<String> users= new HashSet<>();
        users.addAll(expense.getPaidFor());
        users.addAll(expense.getPaidBy().keySet());
        List<UserEntity> userList= userDao.getUsers(users);
        if(userList.size()!=users.size()){
            throw new Exception("Unknown user detected");
        }
        groupDao.getGroup(expense.getGroupId());
    }

    private void updateExpense(Expense expense) {
        for (Map.Entry<String, Integer> ex: expense.getPaidBy().entrySet()) {
            expense.getPaidBy().put(ex.getKey(),percentageCalculator(expense.getPaidAmount(),ex.getValue()));
        }
    }

    private Integer percentageCalculator(int paidAmount, Integer value) {
        return (paidAmount*value)/100;
    }

    private void divide(Expense expense) {
        Map<Integer,String> reverseMap= new TreeMap<>(Collections.reverseOrder());
        putAllTreeMap(reverseMap,expense.getPaidBy());
        float share = (float) expense.getPaidAmount() / expense.getPaidFor().size();
        Queue<ExpenseAmount> expenseAmountQueue= convertToQueue(reverseMap,share);
        for (String user : expense.getPaidFor()) {
            float userShare=share;
            if(expense.getPaidBy().containsKey(user)){
                if(userShare<=expense.getPaidBy().get(user))
                    continue;
                userShare-=expense.getPaidBy().get(user);
                amountUpation(expense, user, userShare, expenseAmountQueue);
            }else{
                amountUpation(expense, user, userShare, expenseAmountQueue);
            }
        }
    }

    private void amountUpation(Expense expense, String user, float userShare, Queue<ExpenseAmount> expenseAmountQueue) {
        while(userShare>0.1f){
            ExpenseAmount currExpenseAmount= expenseAmountQueue.peek();
            if(currExpenseAmount.getAmount()> userShare){
                expenseDao.updateExpense(user,currExpenseAmount.getUser(),userShare, expense.getGroupId());
                currExpenseAmount.setAmount(currExpenseAmount.getAmount()- userShare);
                userShare =0;
            } else if (currExpenseAmount.getAmount()== userShare) {
                expenseDao.updateExpense(user,currExpenseAmount.getUser(),userShare, expense.getGroupId());
                expenseAmountQueue.poll();
                userShare=0;
            }else{
                userShare -= currExpenseAmount.getAmount();
                expenseDao.updateExpense(user,currExpenseAmount.getUser(),currExpenseAmount.getAmount(), expense.getGroupId());
                expenseAmountQueue.poll();
            }

        }
    }

    private void putAllTreeMap(Map<Integer, String> reverseMap, Map<String, Integer> paidBy) {
        paidBy.forEach((key, value) -> {reverseMap.put(value,key);});
    }

    private Queue<ExpenseAmount> convertToQueue(Map<Integer, String> reverseMap,float share) {
        Queue<ExpenseAmount> q= new ArrayDeque<>();
        for (Map.Entry<Integer, String> currObj:
             reverseMap.entrySet()) {
            ExpenseAmount e= new ExpenseAmount();
            e.setUser(currObj.getValue());
            e.setAmount(currObj.getKey()-share);
            if(e.getAmount()<=0)
                continue;
            q.add(e);
        }
        return q;

    }

    public List<ExpenseEntity> getAllExpense() {
        return expenseDao.getAllExpense();
    }

    public void deleteExpense(String uuid) throws Exception {
        ExpenseRecordEntity expenseRecordEntity = expenseDao.getRecord(UUID.fromString(uuid));
        Map<String,Integer> paidBy= convertToPaidBy(expenseRecordEntity.getPaidBy());
        List<ExpenseEntity> expenseEntities= expenseDao.getExpenses(Arrays.stream(expenseRecordEntity.getPaidFor().split(";")).collect(Collectors.toSet()),expenseRecordEntity.getGroupId());
        Map<String,Map<String,ExpenseEntity>> expenseEntityMap= expenseEntityMap(expenseEntities);
        for (Map.Entry<String,Integer> payer: paidBy.entrySet()) {
            String payerName= payer.getKey();
            Integer amount=payer.getValue();
            float share= (float) amount /expenseRecordEntity.getPaidFor().split(";").length;
            for(String perPaid: expenseRecordEntity.getPaidFor().split(";")){
                if(perPaid.equalsIgnoreCase(payerName))
                    continue;
                ExpenseEntity expenseEntity1= expenseEntityMap.get(payerName).get(perPaid);
                ExpenseEntity expenseEntity2= expenseEntityMap.get(perPaid).get(payerName);
                expenseEntity1.setLentAmount(expenseEntity1.getLentAmount()-share);
                expenseEntity2.setLentAmount(expenseEntity2.getLentAmount()+share);
            }
        }
        List<ExpenseEntity> saveEntity= getAllEntity(expenseEntityMap);
        expenseDao.updateAllExpense(saveEntity);
        expenseDao.deleteRecord(UUID.fromString(uuid));
    }

    private List<ExpenseEntity> getAllEntity(Map<String, Map<String, ExpenseEntity>> expenseEntityMap) {
        return expenseEntityMap.entrySet().stream().flatMap(p->p.getValue().values().stream()).toList();
    }

    private Map<String, Map<String, ExpenseEntity>> expenseEntityMap(List<ExpenseEntity> expenseEntities) {
        Map<String, Map<String, ExpenseEntity>> returnMap= expenseEntities.stream().map(ExpenseEntity::getUserId).distinct().collect(Collectors.toMap(e->e,p->new HashMap<>()));
        expenseEntities.forEach(p->{
            String user= p.getUserId();
            String otherUser= p.getOtherUserId();
            Map<String,ExpenseEntity> mapper= returnMap.get(user);
            mapper.put(otherUser,p);
        });
        return returnMap;
    }

    private Map<String, Integer> convertToPaidBy(String paidBy) {
        String[] paidByUser= paidBy.split(";");
        Map<String,Integer> paidByMap= new HashMap<>();
        for (String payer:
             paidByUser) {
            String[] paidByArray= payer.split("_");
            paidByMap.put(paidByArray[0],Integer.parseInt(paidByArray[1]));
        }
        return paidByMap;
    }

    public ExpenseRecordEntity updateThisExpense(String uuid, Expense expense) throws Exception {
        deleteExpense(uuid);
        return createExpense(expense);
    }

    public List<ExpenseRecordEntity> getAllExpenseRecord() {
        return expenseDao.getAllRecord();
    }
}
