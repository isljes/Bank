package com.example.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Email is required field")
    @Size(min = 4,max = 60,message = "Size must be between 4 and 60 symbol")
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Password is required field")
    @Size(min = 7,message = "Password must be more than 7 symbol")
    @Column(name = "password")
    private String password;

    @Transient
    @ToString.Exclude
    private String confirmPassword;

    @ToString.Exclude
    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "userEntity")
    @ToString.Exclude
    private List<CardEntity> cardEntities;

    @OneToOne(mappedBy = "userEntity")
    @ToString.Exclude
    private PersonalDetailsEntity personalDetailsEntity;

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if(o==null||this.getClass()!=o.getClass()) return false;
        UserEntity userEntity=(UserEntity) o;
        return Objects.equals(userEntity.id, this.id);
    }
}
