package com.example.splitwise.controller;

import com.example.splitwise.entity.ExpenseEntity;
import com.example.splitwise.entity.ExpenseRecordEntity;
import com.example.splitwise.manager.ExpenseManager;
import com.example.splitwise.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class ExpenseController {

    @Autowired
    private ExpenseManager expenseManager;

    @PostMapping("v1/expense")
    public ResponseEntity<Object> createExpense(@RequestBody Expense expense){
        try {
            return ResponseEntity.ok(expenseManager.createExpense(expense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("v1/expense")
    public List<ExpenseEntity> getAllExpense(){
        return expenseManager.getAllExpense();
    }

    @DeleteMapping("v1/expense/{uuid}")
    public ResponseEntity<Object> deleteExpense(@PathVariable String uuid){
        try {
            expenseManager.deleteExpense(uuid);
            return ResponseEntity.ok("Expense successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    @PutMapping("v1/expense/{uuid}")
    public ResponseEntity<Object> updateExpense(@PathVariable String uuid,@RequestBody Expense expense){
        try {
            return ResponseEntity.ok( expenseManager.updateThisExpense(uuid,expense));

        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("v1/expenseRecord")
    public List<ExpenseRecordEntity> getAllRecordEntity(){
        return expenseManager.getAllExpenseRecord();
    }
}
