package dev.nightzen.antifraud.business.entity;

import dev.nightzen.antifraud.constants.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "auth_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    Long id;

    @Column
    @NotBlank
    String name;

    @Column
    @NotBlank
    String username;

    @Column
    @NotBlank
    String password;

    @Column
    UserRole role;

    @Column
    Boolean locked;
}
