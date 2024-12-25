package com.example.splitwise.controller;

import com.example.splitwise.model.PatchUser;
import com.example.splitwise.model.User;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserManager userManager;

    @PostMapping("v1/user")
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userDao.addUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("v1/user/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable String userId) {
        Optional<UserEntity> user=userDao.getUser(userId);
        return user.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(NOT_FOUND).body("User not present"));
    }

    @GetMapping("v1/users")
    public List<UserEntity> getAllUsers(){
        return userDao.getAllUser();
    }

    @PatchMapping("v1/add-user-to-group")
    public ResponseEntity<Object> addGroupToUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(userDao.addUserToGroup(user));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("v1/remove-user-from-group")
    public ResponseEntity<Object> removeGroupFromUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(userDao.removeUserFromGroup(user));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("v1/add-friend-to-user")
    public ResponseEntity<Object> addFriendToUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(userDao.addFriendToUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("v1/remove-friend-from-user")
    public ResponseEntity<Object> removeFriendFromUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(userDao.removeFriendFromUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("v1/user/{userId}")
    public  ResponseEntity<Object> deleteUser(@PathVariable String userId){
        try {
            return ResponseEntity.ok(userDao.deleteUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("v1/report/{userId}")
    public ResponseEntity<Object> getUserReport(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(userManager.getUserReport(userId));
        } catch (Exception e) {
           return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }
}
