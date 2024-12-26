package com.example.splitwise.model;

import com.example.splitwise.entity.GroupEntity;
import com.example.splitwise.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Group {
    @JsonProperty("group_name")
    private String name;

    @JsonProperty("users")
    private Set<String> users;

    private List<UserEntity> userEntityList;

    public GroupEntity toGroupEntity(){
        GroupEntity groupEntity= new GroupEntity();
        groupEntity.setGroupName(this.getName());
        groupEntity.setCreationDate(new Date().getTime());
        groupEntity.setUsers(this.getUserEntityList());
        return groupEntity;
    }
}
