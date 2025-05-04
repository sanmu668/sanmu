package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wzs
 * @date 2025年05月03日 16:50
 */
@Data
@Entity
@Table(name = "job_position")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_name")
    private String jobName;

    @Column(name="department")
    private String department;

    @Column(name="salary_range")
    private String salaryRange;

    @Column(name = "experience")
    private String experience;

    @Column(name = "education")
    private String education;

    @Column(name = "job_type")
    private String jobType;

    @Column(name ="description")
    private String description;

    @Column(name = "requirements")
    private String requirements;

    @Column(name = "company")
    private String company;

    @Column(name = "location")
    private String location;

    @Column(name = "tags")
    private String tags;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
