package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wzs
 * @date 2025年04月27日 23:20
 */

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role = Role.user;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updateAt = LocalDateTime.now();

    private LocalDateTime lastLogin;

    public enum Role{
        user,admin
    }
}
