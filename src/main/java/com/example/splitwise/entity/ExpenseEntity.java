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
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    @Column(name="user_id")
    private String userId;
    @Getter
    @Setter
    @Column(name="other_user_id")
    private String otherUserId;
    @Getter
    @Setter
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

    public float getLentAmount() {
        return Float.parseFloat(String.format("%.2f",lentAmount));
    }

    @Override
    public String toString() {
        return "ExpenseEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", otherUserId='" + otherUserId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", lentAmount=" + lentAmount +
                '}';
    }
}
