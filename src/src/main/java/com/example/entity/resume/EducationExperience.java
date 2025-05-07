package com.example.entity.resume;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "education_experience")
public class EducationExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "school_name")
    private String schoolName;

    private String degree;
    private String major;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    private String description;
}
