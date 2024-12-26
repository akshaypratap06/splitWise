package com.example.splitwise.controller;

import com.example.splitwise.ExpenseType;
import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.manager.ExpenseManager;
import com.example.splitwise.model.Expense;
import com.example.splitwise.model.Group;
import com.example.splitwise.model.PatchUser;
import com.example.splitwise.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PreReqController {

    @Autowired
    UserDao userDao;

    @Autowired
    GroupDao groupDao;

    @Autowired
    ExpenseManager expenseManager;
    @GetMapping("v1/prerequisite")
    public String preRequisite() throws Exception {
//        groupDao.createGroup(new Group("default"));
        userDao.addUser(new User("akshay","akshay",new Date()));
        userDao.addUser(new User("pratap","pratap",new Date()));
        userDao.addUser(new User("singh","singh",new Date()));



        userDao.addUserToGroup(new PatchUser("akshay", "default"));
        userDao.addUserToGroup(new PatchUser("pratap","default"));
        userDao.addUserToGroup(new PatchUser("singh","default"));
        Map<String,Float> req1= new HashMap<>();
        req1.put("pratap",400f);
        Map<String,Float> userList= Map.of("akshay",0f,"pratap",0f,"singh",0f);
        Expense expense= new Expense(ExpenseType.EQUAL,req1,new HashMap<>(userList),400,"default",ExpenseType.EQUAL);
        expenseManager.createExpense(expense);
        req1.put("pratap",200f);
        expense= new Expense(ExpenseType.EQUAL,req1,new HashMap<>(Map.of("akshay",0f,"pratap",0f)),200,"default",ExpenseType.EQUAL);
        expenseManager.createExpense(expense);
        req1.remove("pratap");
        req1.put("singh",1000f);
        expense= new Expense(ExpenseType.EQUAL,req1,new HashMap<>(Map.of("akshay",0f,"pratap",0f)),1000,"default",ExpenseType.EQUAL);
        expenseManager.createExpense(expense);
        return "Created all users and groups";
    }
}
