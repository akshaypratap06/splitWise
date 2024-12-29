package com.example.splitwise.dao;

import com.example.splitwise.entity.ExpenseEntity;
import com.example.splitwise.model.SettleUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
public class SettleUpDao {
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public Object settle(SettleUp settleUp) {
        if(settleUp.getType().equalsIgnoreCase("all")){
            settleAll(settleUp.getUser());
        }else if(settleUp.getType().equalsIgnoreCase("group")){
            settleGroup(settleUp.getGroup(),settleUp.getUser());
        }else{
            settleFriend(settleUp.getUser(),settleUp.getFriend());
        }

        return "Done";
    }

    private void settleFriend(String user, String friend) {
        Session s= entityManager.unwrap(Session.class);
        List<ExpenseEntity> expenseEntityList= entityManager.createQuery("select e from ExpenseEntity e where (userId = :userId and otherUserId = :friendId) or (userId = :friendId and otherUserId = :userId) ", ExpenseEntity.class).setParameter("userId",user).setParameter("friendId",friend).getResultList();
        System.out.println(expenseEntityList);
        for(ExpenseEntity e:expenseEntityList){
            e.setLentAmount(0);
            s.merge(e);
        }
    }

    private void settleGroup(String group, String user) {
        Session s= entityManager.unwrap(Session.class);
        List<ExpenseEntity> expenseEntityList= entityManager.createQuery("select e from ExpenseEntity e where (userId = :userId or otherUserId = :userId) and groupId = :groupId", ExpenseEntity.class).setParameter("userId",user).setParameter("groupId",group).getResultList();
        for(ExpenseEntity e:expenseEntityList){
            e.setLentAmount(0);
            s.merge(e);
        }
    }

    private void settleAll(String user) {
        Session s= entityManager.unwrap(Session.class);
        List<ExpenseEntity> expenseEntityList= entityManager.createQuery("select e from ExpenseEntity e where userId = :userId or otherUserId = :userId", ExpenseEntity.class).setParameter("userId",user).getResultList();
        for(ExpenseEntity e:expenseEntityList){
            e.setLentAmount(0);
            s.merge(e);
        }
    }
}
