package com.example.dto;

import lombok.Data;

/**
 * @author wzs
 * @date 2025年04月27日 23:33
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
