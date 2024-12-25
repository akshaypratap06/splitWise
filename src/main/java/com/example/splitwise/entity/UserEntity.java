package com.example.splitwise.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("user_name")
    private String userName;

    @Column(name="email")
    @JsonProperty("email")
    private String email;

    @Column(name="creation_time")
    @JsonProperty("creation_time")
    private Date creationTime;

    @ManyToMany
    @JoinTable(name = "user_groups",joinColumns= @JoinColumn(name="user_name"),inverseJoinColumns = @JoinColumn(name="group_name"))
    @JsonManagedReference
    @JsonProperty("groups")
    private List<GroupEntity> groupEntityList;

    @Column(name="friends")
    @JsonProperty("friends")
    private Set<String> friendEntityList;

    public Set<String> getFriendEntityList() {
        return friendEntityList==null?new HashSet<>():friendEntityList;
    }

    public void setFriendEntityList(Set<String> friendEntityList) {
        this.friendEntityList = friendEntityList==null?new HashSet<>():friendEntityList;
    }
}
