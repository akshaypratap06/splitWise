package com.example.splitwise.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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

    @Column(name="email")
    private String email;

    @Column(name="creation_time")
    private Date creationTime;

    @ManyToMany
    @JoinTable(name = "user_groups",joinColumns= @JoinColumn(name="user_name"),inverseJoinColumns = @JoinColumn(name="group_name"))
    @JsonManagedReference
    private List<GroupEntity> groupEntityList;


}
