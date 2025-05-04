package com.example.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wzs
 * @date 2025年05月02日 23:55
 */
@Data
public class ResumeDTO {
    private Integer id;
    private String userName;
    private String title;
    private List<String> education;
    private String experience;
    private String status;
    private LocalDateTime updateTime;
}
