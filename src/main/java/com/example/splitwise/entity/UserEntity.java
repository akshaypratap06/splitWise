package com.example.splitwise.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "user_table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "creation_time")
    private Date creationTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "user_name"), inverseJoinColumns = @JoinColumn(name = "group_name"))
    private List<GroupEntity> groupEntityList;

    @Column(name = "friends")
    private Set<String> friendEntityList;

    public Set<String> getFriendEntityList() {
        return friendEntityList == null ? new HashSet<>() : friendEntityList;
    }

    public void setFriendEntityList(Set<String> friendEntityList) {
        this.friendEntityList = friendEntityList == null ? new HashSet<>() : friendEntityList;
    }

    public List<GroupEntity> getGroupEntityList() {
        return groupEntityList==null ? new ArrayList<>():groupEntityList;
    }

    public void setGroupEntityList(List<GroupEntity> groupEntityList) {
        this.groupEntityList = groupEntityList == null ? new ArrayList<>() : groupEntityList;
    }
}
