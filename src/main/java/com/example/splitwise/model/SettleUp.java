package com.example.splitwise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettleUp {

    @JsonProperty("type")
    private String type;

    @JsonProperty("group")
    private String group;

    @JsonProperty("friend")
    private String friend;

    @JsonProperty("user")
    private String user;
}
