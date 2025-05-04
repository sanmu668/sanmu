package com.example.repository;

import com.example.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wzs
 * @date 2025年05月02日 23:53
 */
public interface ResumRepository extends JpaRepository<Resume,Integer> {
    Page<Resume> findByTitleContaining(String title, Pageable pageable);
}
