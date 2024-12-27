package com.example.splitwise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class AllTransactionDTO {
    @JsonProperty("description")
    private String description;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("you_owe")
    private float youOwe;

    @JsonProperty("you_owed")
    private float youOwed;

    @JsonProperty("paid_by")
    private Map<String,Float> paidBy;

    @JsonProperty("paid_for")
    private Map<String,Float> paidFor;

    @JsonProperty("total_money")
    private float totalMoney;
}
