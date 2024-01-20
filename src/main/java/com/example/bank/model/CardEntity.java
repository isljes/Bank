package com.example.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Column(name = "cvv")
    private String cvv;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "payment_system")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{jakarta.validation.custom.message.card.payment-system.NotNull}")
    private  PaymentSystem paymentSystem;

    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{jakarta.validation.custom.message.card.card-type.NotNull}")
    private  CardType cardType;

    @Column(name = "balance")
    private long balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity userEntity;

}
