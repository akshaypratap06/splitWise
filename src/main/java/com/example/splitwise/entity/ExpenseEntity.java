package com.example.splitwise.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="expense_table")
@Getter
@Setter

public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @Column(name="user_id")
    private String userId;

    @Column(name="other_user_id")
    private String otherUserId;

    @Column(name="group_id")
    private String groupId;

    @Column(name = "lent_amount")
    private double lentAmount=0.0d;

    public ExpenseEntity(String user,String otherUserId,String groupId,double lentAmount){
        this.userId=user;
        this.otherUserId=otherUserId;
        this.groupId=groupId;
        this.lentAmount=lentAmount;
    }

    public ExpenseEntity(){

    }


}
