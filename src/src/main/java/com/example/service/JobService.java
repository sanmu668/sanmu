package com.example.service;

import com.example.dto.JobDTO;
import com.example.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface JobService {
    Page<Job> findJobs(String jobName, String company, String status, Pageable pageable);

    //创建职位
    @Transactional
    Job createJob(JobDTO jobDTO);

    //更新职位
    @Transactional
    Job updateJob(Integer id, JobDTO jobDTO);

    //删除职位
    @Transactional
    void deleteJob(Integer id);

    //更新职位状态
    @Transactional
    Job updateJobStatus(Integer id, String status);

    //获取职位详情
    Job getJobById(Integer id);
}
