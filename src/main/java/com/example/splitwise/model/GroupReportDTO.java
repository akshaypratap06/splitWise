package com.example.splitwise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupReportDTO {
    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_transaction")
    private List<GroupTransactionDTO> groupTransactionDTOList;

    @JsonProperty("group_members")
    private List<String> groupMembers;

    @JsonProperty("owe")
    private Map<String,Float> oweMap;

}
