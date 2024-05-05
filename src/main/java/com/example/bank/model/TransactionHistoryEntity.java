package com.example.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "transaction_history")
@IdClass(TransactionHistoryID.class)
@NoArgsConstructor
@Data
public class TransactionHistoryEntity implements Serializable {

    @Id @Column(name = "datetime")
    private Timestamp timestamp;

    @Id
    @ManyToOne
    @JoinColumn(name = "card_id")
    @ToString.Exclude
    private CardEntity card;

    @Column(name = "amount")
    private long amount;
}
