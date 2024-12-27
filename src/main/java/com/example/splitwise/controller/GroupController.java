package com.example.splitwise.controller;

import com.example.splitwise.manager.UtilManager;
import com.example.splitwise.model.Group;
import com.example.splitwise.dao.GroupDao;
import com.example.splitwise.model.GroupDTO;
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

    @Autowired
    private UtilManager utilManager;

    @PostMapping("v1/group")
    public ResponseEntity<Object> addGroup(@RequestBody Group group){
        try{
            return ResponseEntity.ok(utilManager.convertToGroupDto(groupDao.createGroup(group),true));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }



    @GetMapping("v1/groups")
    public List<GroupDTO> getAllGroups(){
        return utilManager.convertToGroupDtoList(groupDao.getAllGroup(),true);

    }

    @GetMapping("v1/groups/{group}")
    public ResponseEntity<Object> getGroup(@PathVariable String group){
        try {
            return ResponseEntity.ok(utilManager.convertToGroupDto(groupDao.getGroup(group),true));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }
}
