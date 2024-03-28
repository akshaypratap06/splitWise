package com.example.splitwise.manager;

import com.example.splitwise.dao.ExpenseDao;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserManager {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExpenseDao expenseDao;

    public UserResponse getUserReport(String userId) throws Exception {
        Optional<UserEntity> user=userDao.getUser(userId);
        if(user.isEmpty()){
            throw new Exception("User Not present");
        }
        return expenseDao.getUserReport(userId);
    }
}
