package com.example.splitwise.controller;

import com.example.splitwise.*;
import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;

@RestController
public class PreReqController {

    @Autowired
    UserDao userDao;

    @Autowired
    GroupDao groupDao;
    @GetMapping("v1/prerequisite")
    public String preRequisite() throws Exception {
        userDao.addUser(new User("akshay","akshay",new Date()));
        userDao.addUser(new User("pratap","pratap",new Date()));
        userDao.addUser(new User("singh","singh",new Date()));

        groupDao.createGroup(new Group("default"));

        userDao.patchUser(new PatchUser("akshay", Collections.singletonList("default")));
        userDao.patchUser(new PatchUser("pratap",Collections.singletonList("default")));
        userDao.patchUser(new PatchUser("singh",Collections.singletonList("default")));
        return "Created all users and groups";
    }
}
