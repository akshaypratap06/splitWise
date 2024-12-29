package com.example.splitwise.controller;

import com.example.splitwise.dao.SettleUpDao;
import com.example.splitwise.model.SettleUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class SettleUpController {

    @Autowired
    private SettleUpDao settleUpDao;

    @PostMapping("v1/settle-up")
    public ResponseEntity<Object> postSettleUp(@RequestBody SettleUp settleUp){
        try {
            return ResponseEntity.ok(settleUpDao.settle(settleUp));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }
}
