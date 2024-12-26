package com.example.splitwise.controller;

import com.example.splitwise.manager.UtilManager;
import com.example.splitwise.model.GroupDTO;
import com.example.splitwise.model.PatchUser;
import com.example.splitwise.model.User;
import com.example.splitwise.dao.UserDao;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.manager.UserManager;
import com.example.splitwise.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UtilManager utilManager;
    @Autowired
    private UserManager userManager;
    @PostMapping("v1/user")
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(utilManager.convertToUserDTO(userDao.addUser(user),true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }



    @GetMapping("v1/user/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable String userId) {
        Optional<UserEntity> user=userDao.getUser(userId);
        try {
            if (user.isPresent()) {
                return ResponseEntity.ok(utilManager.convertToUserDTO(user.get(),true));
            } else {
                throw new Exception("User Not Found");
            }
        }catch (Exception e){
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }

    }

    @GetMapping("v1/users")
    public List<UserDTO> getAllUsers(){
        return utilManager.convertToUserDTOList(userDao.getAllUser(),true);
    }

    @PatchMapping("v1/add-user-to-group")
    public ResponseEntity<Object> addGroupToUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(utilManager.convertToUserDTO(userDao.addUserToGroup(user),true));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("v1/remove-user-from-group")
    public ResponseEntity<Object> removeGroupFromUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(utilManager.convertToUserDTO(userDao.removeUserFromGroup(user),true));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("v1/add-friend-to-user")
    public ResponseEntity<Object> addFriendToUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(utilManager.convertToUserDTO(userDao.addFriendToUser(user),true));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("v1/remove-friend-from-user")
    public ResponseEntity<Object> removeFriendFromUser(@RequestBody PatchUser user){
        try {
            return ResponseEntity.ok(utilManager.convertToUserDTO(userDao.removeFriendFromUser(user),true));
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
