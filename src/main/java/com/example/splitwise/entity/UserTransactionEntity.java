package com.example.splitwise.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="user_transaction_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionEntity {
    @EmbeddedId
    private UserTransactionKey userTransactionKey;

    public UserTransactionEntity(String user, UUID id){
        this.userTransactionKey= new UserTransactionKey(user,id);
    }

}
