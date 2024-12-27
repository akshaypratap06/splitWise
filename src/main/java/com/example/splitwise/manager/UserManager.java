package com.example.splitwise.manager;

import com.example.splitwise.dao.ExpenseDao;
import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.entity.GroupEntity;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.model.GroupDTO;
import com.example.splitwise.model.UserDTO;
import com.example.splitwise.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserManager {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private GroupDao groupDao;

    public UserResponse getDashboardReport(String userId) throws Exception {
        Optional<UserEntity> user = userDao.getUser(userId);
        if (user.isEmpty()) {
            throw new Exception("User Not present");
        }
        return expenseDao.getDashboardReport(userId);
    }


    public Object getFriendReport(String userId, String friendId) throws Exception {
        List<UserEntity> users = userDao.getUsers(Set.of(userId, friendId));
        if (users.size() != 2) {
            throw new Exception("Users Not Present");
        }
        return expenseDao.createFriendReport(userId,friendId,users);
    }

    public Object getGroupReport(String groupId,String userId) throws Exception {
        GroupEntity groupEntity= groupDao.getGroup(groupId);
        return expenseDao.createGroupReport(groupEntity,userId);
    }

    public Object getAllReport(String userId) throws Exception {
        Optional<UserEntity> user=userDao.getUser(userId);
        if(user.isEmpty()){
            throw new Exception("User not present");
        }
        return expenseDao.createAllTransactions(userId);
    }
}
