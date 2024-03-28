package com.example.splitwise.model;

import com.example.splitwise.entity.GroupEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Group {
    @JsonProperty("group_name")
    private String name;

    public GroupEntity toGroupEntity(){
        GroupEntity groupEntity= new GroupEntity();
        groupEntity.setGroupName(this.getName());
        groupEntity.setCreationDate(new Date().getTime());
        return groupEntity;
    }
}
