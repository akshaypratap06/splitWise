package com.example.splitwise;

import com.example.splitwise.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserResponse extends UserEntity {

    @JsonProperty("expense")
    Map<String, Map<String,Double>> expenseReport;
}
