package com.example.entity;// src/main/java/com/your/package/entity/ApplicationRecord.java

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications_record")
public class ApplicationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "resume_id", nullable = false)
    private Integer resumeId;

    @Column(name = "job_id", nullable = false)
    private Integer jobId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.待处理;

    @Column(name = "submitted_time", nullable = false)
    private LocalDateTime submittedTime = LocalDateTime.now();

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Column(columnDefinition = "text")
    private String note;

    // 创建状态枚举
    public enum ApplicationStatus {
        待处理, 面试中, 已录用, 已拒绝
    }
}


