package com.example.splitwise.model;

import com.example.splitwise.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class PatchUser extends User {
    @JsonProperty("group")
    private String group;

    @JsonProperty("friend")
    private Set<String> friend;

    public PatchUser(String user, String groups,Set<String> friend){
        this.group=groups;
        this.friend=friend;
        super.setUserName(user);
    }

    public PatchUser(String user, String groups){
        this.group=groups;
        super.setUserName(user);
    }

}
