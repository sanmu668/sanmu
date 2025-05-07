package com.example.service.admin.impl;

import com.example.dto.JobDTO;
import com.example.entity.Job;
import com.example.repository.JobRepository;
import com.example.service.admin.JobService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wzs
 * @date 2025年05月03日 17:01
 */

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    /**
     * 动态查询
     *
     * @param jobName 职位名称
     * @param company 公司名称
     * @param status 职位状态
     * @param pageable 分页信息
     * @return 返回一个分页的JobsMkessage对象列表
     */
    @Override
    public Page<Job> findJobs(String jobName, String company, String status, Pageable pageable){
        /*
        创建一个Specification对象来定义动态查询条件
        使用Lambda表达式实现JPA的Specification<T>接口，用于构造SQL动态查询条件
        root：代表实体JobsMessage的元数据，类似于SQL中的表别名
        query：表示JPA Criteria查询对象
        cb（CriteriaBuilder）：构建SQL条件上的工具
         */
        Specification<Job> spec = (root, query, cb) -> {
            /**初始化一个Predicate列表来存储所有查询条件
             * Predicate表示一个布尔类型的表达式（查询条件）
             * 它是JPA Criteria查询中的核心对象，用于构建灵活、动态的where字句
             */
            List<Predicate> predicates = new ArrayList<>();
            //StringUtils.hasText(status)是Spring框架中org.springframework.util.StringUtils提供的一个静态方法，
            //用于判断一个字符串是否不为null且包含至少一个非空白字符。
            if(StringUtils.hasText(jobName)){
                predicates.add(cb.like(root.get("jobName"),"%" + jobName + "%"));
            }
            if (StringUtils.hasText(company)){
                predicates.add(cb.like(root.get("company"),"%" + company + "%"));
            }
            if(StringUtils.hasText(status)){
                predicates.add(cb.equal(root.get("status"),status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return jobRepository.findAll(spec,pageable);
    }

    //创建职位
    @Override
    @Transactional
    public Job createJob(JobDTO jobDTO){
        Job job = new Job();
        /*
        BeanUtils.copyProperties(jobDTO,job):是Spring框架中的一个常用方法，
        用于对象之间的属性值复制，主要用于DTO（数据传输对象）和实体之间的转换。
         */
        BeanUtils.copyProperties(jobDTO, job);
        return jobRepository.save(job);
    }


    /**
     * 更新职位信息
     * 使用@treansactional注解，如果在执行过程中出现异常，事务会回滚，避免数据不一致的问题
     * @param id
     * @param jobDTO  包含需要更新的职位信息的数据传输对象
     * @return 返回更新后的职位信息
     */
    @Override
    @Transactional
    public Job updateJob(Integer id,JobDTO jobDTO){
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        //从jobDTO复制属性到job对象，排除id、createdAt和updatedAt字段，以保持这些字段的值不变
        BeanUtils.copyProperties(jobDTO,job,"id","createdAt","updatedAt");
        return jobRepository.save(job);
    }

    //删除职位
    @Override
    @Transactional
    public void deleteJob(Integer id){
        jobRepository.deleteById(id);
    }

    //更新职位状态
    @Override
    @Transactional
    public Job updateJobStatus(Integer id,String status){
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        return jobRepository.save(job);
    }

    //获取职位详情
    @Override
    public Job getJobById(Integer id){
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
}
