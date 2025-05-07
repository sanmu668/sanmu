package com.example.dto;

import lombok.Data;

/**
 * @author wzs
 * @date 2025年05月05日 10:23
 */
@Data
public class UserSearchRequest {
    private String username;
    private String email;
    private Integer page = 1;
    private Integer size = 10;
}
