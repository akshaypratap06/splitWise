package com.example.splitwise.dao;

import com.example.splitwise.entity.ExpenseEntity;
import com.example.splitwise.entity.ExpenseRecordEntity;
import com.example.splitwise.entity.UserTransactionEntity;
import com.example.splitwise.model.UserResponse;
import com.example.splitwise.model.Expense;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;

@Repository
public class ExpenseDao {

    @PersistenceContext
    private EntityManager entityManager;


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
    public UserResponse getUserReport(String userID) {
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
}
