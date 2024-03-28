package com.example.splitwise.controller;

import com.example.splitwise.ExpenseType;
import com.example.splitwise.dao.ExpenseDao;
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

import java.util.*;

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
        groupDao.createGroup(new Group("default"));
        userDao.addUser(new User("akshay","akshay",new Date()));
        userDao.addUser(new User("pratap","pratap",new Date()));
        userDao.addUser(new User("singh","singh",new Date()));



        userDao.patchUser(new PatchUser("akshay", Collections.singletonList("default")));
        userDao.patchUser(new PatchUser("pratap",Collections.singletonList("default")));
        userDao.patchUser(new PatchUser("singh",Collections.singletonList("default")));
        Map<String,Integer> req1= new HashMap<>();
        req1.put("pratap",400);
        List<String> userList= List.of("akshay","pratap","singh");
        Expense expense= new Expense(ExpenseType.EQUAL,req1,userList,400,"default");
        expenseManager.createExpense(expense);
        req1.put("pratap",200);
        expense= new Expense(ExpenseType.EQUAL,req1,List.of("akshay","pratap"),200,"default");
        expenseManager.createExpense(expense);
        req1.remove("pratap");
        req1.put("singh",1000);
        expense= new Expense(ExpenseType.EQUAL,req1,List.of("akshay","pratap"),1000,"default");
        expenseManager.createExpense(expense);
        return "Created all users and groups";
    }
}
