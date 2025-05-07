package com.example.controller.user;

import com.example.dto.ApplicationRecordDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.user.ApplicationRecordService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wzs
 * @date 2025年05月05日 12:14
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationRecordController {
    private final ApplicationRecordService applicationRecordService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/sumit")
    public ResponseEntity<?> submit(@RequestBody SubmitRequest request){
        Integer userId = getUserId();
        try{
            ApplicationRecordDTO result = applicationRecordService.submitApplication(userId, request.jobId, request.getResumeId());
            return ResponseEntity.ok(result);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    static class SubmitRequest{
        private Integer jobId;
        private Integer resumeId;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserApplications(){
        Integer userId = getUserId();
        List<ApplicationRecordDTO> applications = applicationRecordService.getUserApplications(userId);
        return ResponseEntity.ok(applications);
    }

    private Integer getUserId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getUserByEmail(email);
        if (user == null){
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }
}
