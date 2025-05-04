package com.example.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wzs
 * @date 2025年05月02日 19:00
 */

@Data
@Entity
@Table(name = "resumes")
public class Resume {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String summary;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<EducationExperience> educationExperience;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<WorkExperience> workExperienceList;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Skill> skills;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Project> projects;

    @OneToMany(mappedBy = "resume",cascade = CascadeType.ALL)
    private List<Certificate> certificate;
}


