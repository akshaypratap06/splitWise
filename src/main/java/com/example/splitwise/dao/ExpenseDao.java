package com.example.splitwise.dao;

import com.example.splitwise.entity.*;
import com.example.splitwise.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ExpenseDao {

    @PersistenceContext
    private EntityManager entityManager;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    @Transactional
    public void updateExpense(String user, String user1, float amount, String groupId) {
        List<ExpenseEntity> expenseEntity = entityManager.createQuery("select u from ExpenseEntity u where u.userId= :userId and u.otherUserId= :otherUserId and u.groupId= :groupId", ExpenseEntity.class).setParameter("userId", user).setParameter("otherUserId", user1).setParameter("groupId", groupId).getResultList();
        List<ExpenseEntity> expenseEntity2 = entityManager.createQuery("select u from ExpenseEntity u where u.userId= :userId and u.otherUserId= :otherUserId and u.groupId= :groupId", ExpenseEntity.class).setParameter("userId", user1).setParameter("otherUserId", user).setParameter("groupId", groupId).getResultList();
        ExpenseEntity e = null;
        ExpenseEntity e2 = null;
        if (expenseEntity == null || expenseEntity.isEmpty()) {
            e = new ExpenseEntity();
            e.setGroupId(groupId);
            e.setUserId(user);
            e.setOtherUserId(user1);
        } else {
            e = expenseEntity.get(0);
        }
        if (expenseEntity2 == null || expenseEntity2.isEmpty()) {
            e2 = new ExpenseEntity();
            e2.setGroupId(groupId);
            e2.setUserId(user1);
            e2.setOtherUserId(user);
        } else {
            e2 = expenseEntity2.get(0);
        }
        e.setLentAmount(e.getLentAmount() - amount);
        e2.setLentAmount(e2.getLentAmount() + amount);
        Session s = entityManager.unwrap(Session.class);
        s.merge(e);
        s.merge(e2);
    }

    @Transactional
    public UserResponse getDashboardReport(String userID) {
        List<ExpenseEntity> expenseEntity = entityManager.createQuery("SELECT e from ExpenseEntity e where e.userId= :userId", ExpenseEntity.class).setParameter("userId", userID).getResultList();
        return mapToUserResponse(expenseEntity, userID);
    }

    private UserResponse mapToUserResponse(List<ExpenseEntity> expenseEntity, String userId) {
        Map<String, Map<String, Double>> expense = new HashMap<>();
        float totalBalance=0.0f;
        Map<String,Float> oweOwedCalculationMap= new HashMap<>();
        Map<String,Float> userOwe=new HashMap<>();
        Map<String,Float> userOwed=new HashMap<>();
        float amountOwe=0.0f;
        float amountOwed=0.0f;
        for (ExpenseEntity ex :
                expenseEntity) {
            totalBalance+=Float.parseFloat(String.format("%.2f", ex.getLentAmount()));
            if(ex.getLentAmount()>0){
                amountOwed+=Float.parseFloat(String.format("%.2f", ex.getLentAmount()));
            }else{
                amountOwe+=Float.parseFloat(String.format("%.2f", Math.abs(ex.getLentAmount())));
            }
            oweOwedCalculationMap.put(ex.getOtherUserId(),oweOwedCalculationMap.getOrDefault(ex.getOtherUserId(),0.0f)+ex.getLentAmount());
            if (expense.containsKey(ex.getGroupId())) {
                expense.get(ex.getGroupId()).put(ex.getOtherUserId(), Double.parseDouble(String.format("%.2f", ex.getLentAmount())));
            } else {
                Map<String, Double> userExpense = new HashMap<>();
                userExpense.put(ex.getOtherUserId(),  Double.parseDouble(String.format("%.2f", ex.getLentAmount())));
                expense.put(ex.getGroupId(), userExpense);
            }
        }
        for(Map.Entry<String,Float> currEntry: oweOwedCalculationMap.entrySet()){
            if(currEntry.getValue()<0){
                userOwe.put(currEntry.getKey(),Math.abs(currEntry.getValue()));
            }else if(currEntry.getValue()>0){
                userOwed.put(currEntry.getKey(),Math.abs(currEntry.getValue()));
            }
        }
        UserResponse userResponse = new UserResponse();
        userResponse.setUserName(userId);
        userResponse.setExpenseReport(expense);
        userResponse.setTotalBalance(Math.abs(totalBalance));
        userResponse.setYouOwe(Math.abs(amountOwe));
        userResponse.setYouAreOwed(Math.abs(amountOwed));
        userResponse.setYouOweMap(userOwe);
        userResponse.setYouOwedMap(userOwed);
        return userResponse;
    }

    @Transactional
    public List<ExpenseEntity> getAllExpense() {
        return entityManager.createQuery("select g from ExpenseEntity g", ExpenseEntity.class).getResultList();
    }

    @Transactional
    public ExpenseRecordEntity saveRecord(Expense expense,UUID id) {
        Session s = entityManager.unwrap(Session.class);
        return s.merge(expense.toExpenseRecordEntity(id));
    }

    @Transactional
    public void deleteRecord(UUID uuid) {
        entityManager.createQuery("delete from ExpenseRecordEntity where id=:uuid").setParameter("uuid", uuid).executeUpdate();
    }

    @Transactional
    public ExpenseRecordEntity getRecord(UUID uuid) throws Exception {

        Optional<ExpenseRecordEntity> expenseRecordEntity= Optional.ofNullable(entityManager.createQuery("select e from ExpenseRecordEntity e where id=:uuid", ExpenseRecordEntity.class).setParameter("uuid", uuid).getSingleResult());
        if(expenseRecordEntity.isEmpty()){
            throw new Exception("Unable to find expense record");
        }
        return expenseRecordEntity.get();
    }

    @Transactional
    public List<ExpenseEntity> getExpenses(Set<String> users, String groupId) {
        return entityManager.createQuery("select e from ExpenseEntity e where e.userId in (:userId) and e.groupId= :groupId", ExpenseEntity.class).setParameter("userId", users).setParameter("groupId", groupId).getResultList();
    }

    @Transactional
    public void updateAllExpense(List<ExpenseEntity> saveEntity) {
        Session s = entityManager.unwrap(Session.class);
        for (ExpenseEntity ex :
                saveEntity) {
            s.merge(ex);
        }
    }

    public List<ExpenseRecordEntity> getAllRecord() {
        return entityManager.createQuery("select e from ExpenseRecordEntity e",ExpenseRecordEntity.class).getResultList();
    }
    @Transactional
    public void saveUserTransaction(UUID id, Set<String> users) {
        Session s = entityManager.unwrap(Session.class);
        for (String user:users){
            s.merge(new UserTransactionEntity(user,id));
        }
    }
    @Transactional
    public Object createFriendReport(String userId, String friendId, List<UserEntity> users) {
        Session s = entityManager.unwrap(Session.class);
        String getTransaction = "SELECT t1.userTransactionKey.transactionId " +
                "FROM UserTransactionEntity t1, UserTransactionEntity t2 " +
                "WHERE t1.userTransactionKey.transactionId = t2.userTransactionKey.transactionId " +
                "AND t1.userTransactionKey.userId = :user1 AND t2.userTransactionKey.userId = :user2";
        Query<UUID> query = s.createQuery(getTransaction, UUID.class);
        query.setParameter("user1", userId);
        query.setParameter("user2", friendId);
        List<UUID> transactionIdList= query.getResultList();
        List<ExpenseRecordEntity> transactionList= entityManager.createQuery("Select e from ExpenseRecordEntity e where id in (:list)",ExpenseRecordEntity.class).setParameter("list",transactionIdList).getResultList();
        FriendReportDTO friendReportDTO = new FriendReportDTO();
        friendReportDTO.setFriendName(friendId);
        List<FriendTransactionDTO> friendTransactionDTOList= new ArrayList<>();
        for(ExpenseRecordEntity expenseRecordEntity:transactionList){
            float currOwe=0.0f;
            Map<String,Float> paidBy= getListOfMap(expenseRecordEntity.getPaidBy());
            Map<String,Float> paidFor= getListOfMap(expenseRecordEntity.getPaidFor());
            for(Map.Entry<String,Float> currEntry: paidBy.entrySet()){
                if(currEntry.getKey().equalsIgnoreCase(userId)){
                    currOwe+=currEntry.getValue();
                }
            }
            for(Map.Entry<String,Float> currEntry: paidFor.entrySet()){
                if(currEntry.getKey().equalsIgnoreCase(userId)){
                    currOwe-=currEntry.getValue();
                }
            }
            FriendTransactionDTO friendTransactionDTO= new FriendTransactionDTO();
            friendTransactionDTO.setDescription(expenseRecordEntity.getDescription());
            friendTransactionDTO.setTotalMoney(expenseRecordEntity.getPaidAmount());
            friendTransactionDTO.setDateTime(expenseRecordEntity.getDateTime().format(FORMATTER));
            friendTransactionDTO.setPaidBy(paidBy);
            friendTransactionDTO.setPaidFor(paidFor);
            friendTransactionDTO.setYouOwed(currOwe>=0?Math.abs(currOwe):0);
            friendTransactionDTO.setYouOwe(currOwe<0?Math.abs(currOwe):0);
            friendTransactionDTOList.add(friendTransactionDTO);
        }
        float lentAmount = getExpense(userId,friendId,s);
        friendReportDTO.setFriendOwes(lentAmount>=0?Math.abs(lentAmount):0);
        friendReportDTO.setYouOwe(lentAmount<0?Math.abs(lentAmount):0);
        friendReportDTO.setFriendTransactionDTOList(friendTransactionDTOList);
        return friendReportDTO;

    }

    private float getExpense(String userId, String friendId, Session s) {
        List<ExpenseEntity> expenseEntityList= entityManager.createQuery("select e from ExpenseEntity e where e.userId = :userId and e.otherUserId = :otherUserId",ExpenseEntity.class).setParameter("userId",userId).setParameter("otherUserId",friendId).getResultList();
        float allOwe=0f;
        for (ExpenseEntity e:expenseEntityList){
            allOwe+=e.getLentAmount();
        }
        return allOwe;
    }

    private Map<String, Float> getListOfMap(String paidString) {
        List<String> peoplePayList= Arrays.stream(paidString.split(";")).toList();
        Map<String,Float> finalPeoplePay= new HashMap<>();
        for (String currPeoplePay: peoplePayList){
            List<String> currPeoplePaySplit=Arrays.stream(currPeoplePay.split("_")).toList();
            finalPeoplePay.put(currPeoplePaySplit.get(0),Float.parseFloat(currPeoplePaySplit.get(1)));
        }
        return finalPeoplePay;
    }
    @Transactional
    public Object createGroupReport(GroupEntity groupEntity,String userId) {
        Session s= entityManager.unwrap(Session.class);
        List<ExpenseRecordEntity> expenseRecordEntities= s.createQuery("select e from ExpenseRecordEntity e where groupId = :groupId",ExpenseRecordEntity.class).setParameter("groupId",groupEntity.getGroupName()).getResultList();
        List<ExpenseEntity> expenseEntityList = s.createQuery("select e from ExpenseEntity e where e.lentAmount > 0 and e.groupId = :groupId",ExpenseEntity.class).setParameter("groupId",groupEntity.getGroupName()).getResultList();
        GroupReportDTO groupReportDTO= new GroupReportDTO();
        groupReportDTO.setGroupName(groupEntity.getGroupName());
        groupReportDTO.setGroupMembers(groupEntity.getUsers().stream().map(UserEntity::getUserName).collect(Collectors.toList()));
        groupReportDTO.setOweMap(expenseEntityList.stream().collect(Collectors.toMap(e->e.getUserId()+"_"+e.getOtherUserId(),ExpenseEntity::getLentAmount)));
        List<GroupTransactionDTO> groupTransactionDTOS= new ArrayList<>();
        groupReportDTO.setGroupTransactionDTOList(groupTransactionDTOS);
        for (ExpenseRecordEntity expenseRecordEntity:expenseRecordEntities){
            float currOwe=0.0f;
            Map<String,Float> paidBy= getListOfMap(expenseRecordEntity.getPaidBy());
            Map<String,Float> paidFor= getListOfMap(expenseRecordEntity.getPaidFor());
            for(Map.Entry<String,Float> currEntry: paidBy.entrySet()){
                if(currEntry.getKey().equalsIgnoreCase(userId)){
                    currOwe+=currEntry.getValue();
                }
            }
            for(Map.Entry<String,Float> currEntry: paidFor.entrySet()){
                if(currEntry.getKey().equalsIgnoreCase(userId)){
                    currOwe-=currEntry.getValue();
                }
            }
            GroupTransactionDTO groupTransactionDTO= new GroupTransactionDTO();
            groupTransactionDTO.setDescription(expenseRecordEntity.getDescription());
            groupTransactionDTO.setTotalMoney(expenseRecordEntity.getPaidAmount());
            groupTransactionDTO.setDateTime(expenseRecordEntity.getDateTime().format(FORMATTER));
            groupTransactionDTO.setPaidBy(paidBy);
            groupTransactionDTO.setPaidFor(paidFor);
            groupTransactionDTO.setYouOwed(currOwe>=0?Math.abs(currOwe):0);
            groupTransactionDTO.setYouOwe(currOwe<0?Math.abs(currOwe):0);
            groupTransactionDTOS.add(groupTransactionDTO);
        }
        return groupReportDTO;
    }
    @Transactional
    public Object createAllTransactions(String userId) {
        Session s = entityManager.unwrap(Session.class);
        String getTransaction = "SELECT t1.userTransactionKey.transactionId " +
                "FROM UserTransactionEntity t1 " +
                "WHERE t1.userTransactionKey.userId = :user1";
        Query<UUID> query = s.createQuery(getTransaction, UUID.class);
        query.setParameter("user1", userId);
        List<UUID> transactionIdList= query.getResultList();
        List<ExpenseRecordEntity> transactionList= entityManager.createQuery("Select e from ExpenseRecordEntity e where id in (:list)",ExpenseRecordEntity.class).setParameter("list",transactionIdList).getResultList();
        AllTransactionReportDTO allTransactionReportDTO = new AllTransactionReportDTO();
        List<AllTransactionDTO> allTransactionDTOS= new ArrayList<>();
        for(ExpenseRecordEntity expenseRecordEntity:transactionList){
            float currOwe=0.0f;
            Map<String,Float> paidBy= getListOfMap(expenseRecordEntity.getPaidBy());
            Map<String,Float> paidFor= getListOfMap(expenseRecordEntity.getPaidFor());
            for(Map.Entry<String,Float> currEntry: paidBy.entrySet()){
                if(currEntry.getKey().equalsIgnoreCase(userId)){
                    currOwe+=currEntry.getValue();
                }
            }
            for(Map.Entry<String,Float> currEntry: paidFor.entrySet()){
                if(currEntry.getKey().equalsIgnoreCase(userId)){
                    currOwe-=currEntry.getValue();
                }
            }
            AllTransactionDTO allTransactionDTO= new AllTransactionDTO();
            allTransactionDTO.setDescription(expenseRecordEntity.getDescription());
            allTransactionDTO.setTotalMoney(expenseRecordEntity.getPaidAmount());
            allTransactionDTO.setDateTime(expenseRecordEntity.getDateTime().format(FORMATTER));
            allTransactionDTO.setPaidBy(paidBy);
            allTransactionDTO.setPaidFor(paidFor);
            allTransactionDTO.setYouOwed(currOwe>=0?Math.abs(currOwe):0);
            allTransactionDTO.setYouOwe(currOwe<0?Math.abs(currOwe):0);
            allTransactionDTOS.add(allTransactionDTO);
        }
        float lentAmount = getExpense(userId);
        allTransactionReportDTO.setFriendOwes(lentAmount>=0?Math.abs(lentAmount):0);
        allTransactionReportDTO.setYouOwe(lentAmount<0?Math.abs(lentAmount):0);
        allTransactionReportDTO.setAllTransactionDTOS(allTransactionDTOS);
        return allTransactionReportDTO;
    }

    private float getExpense(String userId) {
        List<ExpenseEntity> expenseEntityList= entityManager.createQuery("select e from ExpenseEntity e where e.userId = :userId",ExpenseEntity.class).setParameter("userId",userId).getResultList();
        float allOwe=0f;
        for (ExpenseEntity e:expenseEntityList){
            allOwe+=e.getLentAmount();
        }
        return allOwe;
    }
}
