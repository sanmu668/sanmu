package com.example.repository;

import com.example.entity.ApplicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author wzs
 * @date 2025年05月05日 11:44
 */
public interface ApplicationRecordRepository extends JpaRepository<ApplicationRecord,Integer> {
    boolean existsByUserIdAndJobId(Integer userId,Integer jobId);
    List<ApplicationRecord> findByUserId(Integer userId);
    List<ApplicationRecord> findByJobId(Integer id);
}
