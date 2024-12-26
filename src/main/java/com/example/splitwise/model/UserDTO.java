package com.example.splitwise.model;

import com.example.splitwise.entity.GroupEntity;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    @JsonProperty("user_name")
    private String userName;


    @JsonProperty("email")
    private String email;

    @JsonProperty("creation_time")
    private Date creationTime;

    @JsonProperty("groups")
    private List<GroupDTO> groupEntityList;

    @JsonProperty("friends")
    private Set<String> friendEntityList;

    public UserDTO(String userName, String email, Date creationTime, Set<String> friendEntityList) {
        this.userName=userName;
        this.email=email;
        this.creationTime=creationTime;
        this.friendEntityList=friendEntityList;
    }

    public Set<String> getFriendEntityList() {
        return friendEntityList == null ? new HashSet<>() : friendEntityList;
    }

    public void setFriendEntityList(Set<String> friendEntityList) {
        this.friendEntityList = friendEntityList == null ? new HashSet<>() : friendEntityList;
    }
}
