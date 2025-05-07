package com.example.entity.resume;

import com.example.entity.User;
import jakarta.persistence.*;
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

    @Column(name = "templateName")
    private String templateName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name", nullable = false)
    private String fileName;  // 系统生成的唯一文件名

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;  // 原始文件名


    @Column(name = "file_size", nullable = false)
    private Long fileSize;  // 文件大小

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


