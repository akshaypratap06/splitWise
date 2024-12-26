package com.example.splitwise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserTransactionKey implements Serializable {
    @Column(name = "user_id")
    private String userId;
    @Column(name = "transaction_id")
    private UUID transactionId;
}
