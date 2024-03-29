package com.example.splitwise.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="expense_table")
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
    private float lentAmount=0.0f;

    public ExpenseEntity(String user,String otherUserId,String groupId,float lentAmount){
        this.userId=user;
        this.otherUserId=otherUserId;
        this.groupId=groupId;
        this.lentAmount=lentAmount;
    }

    public ExpenseEntity(){

    }

    public void setLentAmount(float lentAmount){
        this.lentAmount= Float.parseFloat(String.format("%.2f",lentAmount));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public float getLentAmount() {
        return lentAmount;
    }
}
