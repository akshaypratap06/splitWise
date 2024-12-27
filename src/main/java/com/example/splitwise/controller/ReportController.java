package com.example.splitwise.controller;

import com.example.splitwise.manager.UserManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class ReportController {

    @Autowired
    private UserManager userManager;

    @GetMapping("v1/dashboard/report/{userId}")
    public ResponseEntity<Object> getDashboardReport(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userManager.getDashboardReport(userId));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("v1/friend/report/{userId}/{friendId}")
    public ResponseEntity<Object> getFreindReport(@PathVariable String userId,@PathVariable String friendId) {
        try {
            return ResponseEntity.ok(userManager.getFriendReport(userId,friendId));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("v1/group/report/{groupId}/{userId}")
    public ResponseEntity<Object> getGroupReport(@PathVariable String groupId,@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userManager.getGroupReport(groupId,userId));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("v1/all/report/{userId}")
    public ResponseEntity<Object> getGroupReport(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userManager.getAllReport(userId));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }
}
