package com.example.splitwise.entity;

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
@Entity
@Table(name = "group_table")
public class GroupEntity {

    @Id
    @Column(name = "group_name")
    private String groupName;

    @Column(name = "creation_date")
    private long creationDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "group_name"), inverseJoinColumns = @JoinColumn(name = "user_name"))
    @JsonIgnore
    private List<UserEntity> users = new ArrayList<>();


}
