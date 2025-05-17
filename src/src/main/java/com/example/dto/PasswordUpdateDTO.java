package com.example.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword; // 确认新密码
}
