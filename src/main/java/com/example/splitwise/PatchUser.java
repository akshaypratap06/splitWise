package com.example.splitwise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUser extends User{
    @JsonProperty("groups")
    private List<String> groups;

    public PatchUser(String user, List<String> groups){
        this.groups=groups;
        super.setUserName(user);
    }

}
