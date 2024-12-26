package com.example.splitwise.model;

import com.example.splitwise.entity.UserEntity;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("creation_date")
    private long creationDate;
    @JsonProperty("users")
    private List<UserDTO> users = new ArrayList<>();


    public GroupDTO(String groupName, long creationDate) {
        this.groupName =groupName;
        this.creationDate=creationDate;
    }
}
