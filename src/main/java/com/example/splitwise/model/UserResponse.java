package com.example.splitwise.model;

import com.example.splitwise.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserResponse extends UserDTO {

    @JsonProperty("expense")
    Map<String, Map<String, Double>> expenseReport;

    @JsonProperty("you_owe_map")
    Map<String, Float> youOweMap;
    @JsonProperty("you_owed_map")
    Map<String, Float> youOwedMap;
    @JsonProperty("total_balance_money")
    float totalBalance;
    @JsonProperty("you_owe_money")
    float youOwe;
    @JsonProperty("you_owed_money")
    float youAreOwed;
}
