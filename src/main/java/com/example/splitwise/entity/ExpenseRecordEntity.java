package com.example.splitwise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name="expense_record_table")
@Getter
@Setter
public class ExpenseRecordEntity {

    @Id
    @Column(name = "id")
    UUID id;
    @Column(name = "paid_by")
    private String paidBy;
    @Column(name = "paid_for")
    private String paidFor;
    @Column(name = "paid_amount")
    private float paidAmount;
    @Column(name = "group_id")
    private String groupId;

    @Column(name="description")
    private String description;

    @Column(name="date_time")
    private LocalDateTime dateTime;

}
