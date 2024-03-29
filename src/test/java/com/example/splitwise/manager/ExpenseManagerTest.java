package com.example.splitwise.manager;

import com.example.splitwise.model.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ExpenseManagerTest {

    @InjectMocks
    private ExpenseManager expenseManager;

    @Test
    public void updateExpenseByPercentageTest(){
        Map<String,Float> map= new HashMap<>();
        map.put("akshay",15f);
        map.put("pratap",52f);
        map.put("singh",33f);
        expenseManager.updateExpenseByPercentage(map,1000);
        assertEquals(150f,map.get("akshay"));
        assertEquals(520f,map.get("pratap"));
        assertEquals(330f,map.get("singh"));
    }

    @Test
    public void updateExpensePaidForEqualTest(){
        Map<String,Float> map= new HashMap<>();
        map.put("akshay",0f);
        map.put("pratap",0f);
        map.put("singh",0f);
        expenseManager.updateExpensePaidForEqual(map,1000f);
        assertEquals(333.33f,map.get("akshay"));
        assertEquals(333.33f,map.get("pratap"));
        assertEquals(333.33f,map.get("singh"));
    }


}