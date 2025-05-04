package com.example.dto;

import lombok.Data;

@Data
public class JobDTO {
    private Long id;
    private String jobName;
    private String department;
    private String salaryRange;
    private String experience;
    private String education;
    private String jobType;
    private String description;
    private String requirements;
    private String company;
    private String location;
    private String tags;
    private String status;
}