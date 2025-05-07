package com.example.entity.resume;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "name")
    private String name;
}
