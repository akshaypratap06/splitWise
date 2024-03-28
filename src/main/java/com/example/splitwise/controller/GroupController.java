package com.example.splitwise.controller;

import com.example.splitwise.Group;
import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.entity.GroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class GroupController {

    @Autowired
    private GroupDao groupDao;

    @PostMapping("v1/group")
    public ResponseEntity<Object> addGroup(@RequestBody Group group){
        try{
            return ResponseEntity.ok(groupDao.createGroup(group));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("v1/groups")
    public List<GroupEntity> getAllGroups(){
        return groupDao.getAllGroup();

    }

    @GetMapping("v1/groups/{group}")
    public ResponseEntity<Object> getAllUsers(@PathVariable String group){
        try {
            return ResponseEntity.ok(groupDao.getGroup(group).getUsers());
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }
}
