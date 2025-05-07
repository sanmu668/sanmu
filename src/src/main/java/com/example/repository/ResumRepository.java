package com.example.repository;

import com.example.dto.ResumeDTO;
import com.example.entity.resume.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author wzs
 * @date 2025年05月02日 23:53
 */
public interface ResumRepository extends JpaRepository<Resume,Integer> {
    Page<Resume> findByTitleContaining(String title, Pageable pageable);

    List<Resume> findByUserId(Integer id);
}
