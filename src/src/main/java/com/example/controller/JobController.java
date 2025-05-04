package com.example.controller;

import com.example.dto.JobDTO;
import com.example.entity.Job;
import com.example.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wzs
 * @date 2025年05月03日 17:04
 */

@RestController
@RequestMapping("/api/admin/job")
public class JobController {

    @Autowired
    private JobService jobService;

    /*
    ResponseEntity<?>是Spring Framework中用于表示HTTP响应的一个类，
    通常用来封装HTTP响应体、响应头以及HTTp状态码。
    它是Spring Web中响应实体（Response Entity）的一个通用形式，常用于构建RESTful API的响应
     */
    @GetMapping
    public ResponseEntity<?> getJobs(
            @RequestParam(required = false) String jobName,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pagesize) {
        /**
         * PageRequest.of()
         * Spring Data提供的静态工厂方法，用于创建一个分页请求对象PageRequest
         * 它是Pageable接口的一个常用实现类
         */
        PageRequest pageRequest = PageRequest.of(page - 1, pagesize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Job> jobs = jobService.findJobs(jobName, company, status, pageRequest);
        return ResponseEntity.ok(jobs);   //返回的jobs会自动被转换为JSON对象（SpringBoot默认使用Jackson）
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobDTO jobDTO){
        Job job = jobService.createJob(jobDTO);
        return ResponseEntity.ok(job);
    }

    /**
     * 更新指定ID的工作信息
     *
     * 通过PUT请求接收一个工作信息的更新数据，并将其应用于系统中的一个特定工作实体
     * 该方法首先根据提供的ID检索现有工作实体，然后使用传入的工作数据传输对象（JobDTO）中的数据进行更新
     *
     * @param id 要更新的工作的唯一标识符
     * @param jobDTO 包含要更新的工作信息的数据传输对象
     * @return 返回一个包含更新后的工作实体的响应实体
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Integer id,@RequestBody JobDTO jobDTO){
        // 调用服务层方法更新工作信息，并将更新后的实体返回
        Job job = jobService.updateJob(id, jobDTO);
        // 返回一个状态码为200 OK的响应，包含更新后的实体
        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Integer id){
        jobService.deleteJob(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新职位状态
     *
     * @param id 职位标识符
     * @param jobDTO 包含作业状态信息的DTO对象，用于提供更新后的状态信息
     * @return 返回更新后的职位对象，表示更新操作成功完成
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateJobStatus(
            @PathVariable Integer id,
            @RequestBody JobDTO jobDTO){
        Job job = jobService.updateJobStatus(id, jobDTO.getStatus());
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJob(@PathVariable Integer id){
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
}
