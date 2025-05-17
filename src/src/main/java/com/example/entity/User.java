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

    private String username;

    private String email;

    private String passwordHash;

    @Column(name = "number",length = 11)
    private String number;    //号码

    @Lob
    @Column(name = "photo")
    private byte[] photo;   //照片

    @Enumerated(EnumType.STRING)  //JPA注解，用于指定枚举类型字段未字符串形式存储到数据库中
    private Role role = Role.user;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updateAt = LocalDateTime.now();

    private LocalDateTime lastLogin;

    @Column(columnDefinition = "TEXT")
    private String bio;

    public enum Role{
        user,admin
    }
}
