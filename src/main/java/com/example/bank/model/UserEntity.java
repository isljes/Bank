package com.example.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "{jakarta.validation.custom.message.user.email.NotBlank}")
    @Size(max = 50,message = "{jakarta.validation.custom.message.user.email.Size}")
    @Email(message = "{jakarta.validation.custom.message.user.email.Email}")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "{jakarta.validation.custom.message.user.password.NotBlank}")
    @Size(min = 7,message = "{jakarta.validation.custom.message.user.password.Size}")
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
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "userEntity",fetch = FetchType.EAGER)
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
