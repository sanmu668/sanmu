package com.example.controller.admin;

import com.example.dto.ApplicationRecordDTO;
import com.example.service.admin.UserApplicationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wzs
 * @date 2025年05月07日 16:05
 */
@RestController
@RequestMapping("/api/admin/applications")
public class UserApplicationRecordController {

    @Autowired
    private UserApplicationRecordService application;
    @GetMapping
    public ResponseEntity<?> getUserApplications(){
        List<ApplicationRecordDTO> applications = application.getUserApplications();
        return ResponseEntity.ok(applications);
    }
}
