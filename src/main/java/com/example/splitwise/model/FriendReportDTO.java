package com.example.splitwise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FriendReportDTO {
    @JsonProperty("friend_name")
    private String friendName;

    @JsonProperty("friend_transaction")
    private List<FriendTransactionDTO> friendTransactionDTOList;

    @JsonProperty("friend_owe_you")
    private float friendOwes;

    @JsonProperty("you_owe_friend")
    private float youOwe;
}
