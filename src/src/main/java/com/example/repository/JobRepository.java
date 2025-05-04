package com.example.repository;

import com.example.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRepository extends JpaRepository<Job,Integer>, JpaSpecificationExecutor<Job> {
}
