package com.example.service.user;

import com.example.dto.JobDTO;
import com.example.dto.JobSearchRequest;
import com.example.entity.Job;
import com.example.repository.JobRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author wzs
 * @date 2025年05月05日 0:11
 */
@Service
public class JobPositionService {
    private final JobRepository jobRepository;
    public JobPositionService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Page<JobDTO> searchJobs(JobSearchRequest request){
        PageRequest pageRequest = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"),1));
            if (StringUtils.hasText(request.getJobName())){
                predicates.add(cb.like(root.get("jobName"),"%" + request.getJobName() + "%"));
            }
            if (StringUtils.hasText(request.getLocation())){
                predicates.add(cb.like(root.get("location"),"%" + request.getLocation() + "%"));
            }
            if (StringUtils.hasText(request.getExperience())){
                predicates.add(cb.like(root.get("experience"),"%" + request.getExperience() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Job> jobs = jobRepository.findAll(spec,pageRequest);
        return jobs.map(this::convertToDTO);
    }

    private JobDTO convertToDTO(Job job){
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        BeanUtils.copyProperties(job,dto);
        return dto;
    }
}
