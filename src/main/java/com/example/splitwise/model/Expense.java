package com.example.splitwise.model;

import com.example.splitwise.entity.ExpenseRecordEntity;
import com.example.splitwise.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Expense {
    @JsonProperty("type")
    private ExpenseType type;
    @JsonProperty("paid_by")
    private Map<String,Integer> paidBy;
    @JsonProperty("paid_for")
    private List<String> paidFor;
    @JsonProperty("paid_amount")
    private int paidAmount;
    @JsonProperty("group_id")
    private String groupId;

    public ExpenseRecordEntity toExpenseRecordEntity(){
        ExpenseRecordEntity e= new ExpenseRecordEntity();
        e.setPaidAmount(this.paidAmount);
        e.setPaidBy(convertPaid(this.getPaidBy()));
        e.setPaidFor(String.join(";",this.paidFor));
        e.setGroupId(this.groupId);
        return e;
    }

    private String convertPaid(Map<String, Integer> paidBy) {
        List<String> paidByList= new ArrayList<>();
        for (Map.Entry<String, Integer> currObj:
             paidBy.entrySet()) {
            paidByList.add(currObj.getKey().trim()+"_"+currObj.getValue());
        }
        return String.join(";",paidByList);
    }
}
