package com.example.controller.user;

import com.example.dto.JobDTO;
import com.example.dto.JobSearchRequest;
import com.example.service.user.JobPositionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wzs
 * @date 2025年05月05日 0:36
 */
@RestController
@RequestMapping("/api/user/jobs")
public class UserJobController {
    private final JobPositionService jobPositionService;

    public UserJobController(JobPositionService jobPositionService) {
        this.jobPositionService = jobPositionService;
    }
    @GetMapping
    public ResponseEntity<Page<JobDTO>> searchJobs(JobSearchRequest request){
        return ResponseEntity.ok(jobPositionService.searchJobs(request));
    }
}
