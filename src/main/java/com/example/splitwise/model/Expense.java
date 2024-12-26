package com.example.splitwise.model;

import com.example.splitwise.entity.ExpenseRecordEntity;
import com.example.splitwise.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @JsonProperty("paid_by_type")
    private ExpenseType paidByType;
    @JsonProperty("paid_by")
    private Map<String,Float> paidBy;
    @JsonProperty("paid_for")
    private Map<String,Float> paidFor;
    @JsonProperty("paid_amount")
    private float paidAmount;
    @JsonProperty("group_id")
    private String groupId;
    @JsonProperty("paid_for_type")
    private ExpenseType paidForType;

    public ExpenseRecordEntity toExpenseRecordEntity(UUID id){
        ExpenseRecordEntity e= new ExpenseRecordEntity();
        e.setId(id);
        e.setPaidAmount(Float.parseFloat(String.format("%.2f",this.paidAmount)));
        e.setPaidBy(convertPaid(this.getPaidBy()));
        e.setPaidFor(convertPaid(this.paidFor));
        e.setGroupId(this.groupId);
        return e;
    }

    private String convertPaid(Map<String, Float> paidBy) {
        List<String> paidByList= new ArrayList<>();
        for (Map.Entry<String, Float> currObj:
             paidBy.entrySet()) {
            paidByList.add(currObj.getKey().trim()+"_"+String.format("%.2f",currObj.getValue()));
        }
        return String.join(";",paidByList);
    }
}
