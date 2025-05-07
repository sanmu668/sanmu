package com.example.service.admin;

import com.example.dto.ApplicationRecordDTO;
import com.example.entity.ApplicationRecord;
import com.example.entity.Job;
import com.example.entity.User;
import com.example.repository.ApplicationRecordRepository;
import com.example.repository.JobRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wzs
 * @date 2025年05月07日 15:40
 */
@Service
public class UserApplicationRecordService {

    @Autowired
    private ApplicationRecordRepository applicationRecordRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ApplicationRecordDTO> getUserApplications(){
        return applicationRecordRepository.findAll().stream()
                .map(this::converToDTO)
                .collect(Collectors.toList());
    }

    private ApplicationRecordDTO converToDTO(ApplicationRecord record) {
        ApplicationRecordDTO dto = new ApplicationRecordDTO();
        BeanUtils.copyProperties(record,dto);

        Job job = jobRepository.findById(record.getJobId()).orElse(null);

        User user = userRepository.findById(record.getUserId()).orElse(null);

        if (job != null && user != null){
            dto.setJobName(job.getJobName());
            dto.setCompany(job.getCompany());
            dto.setUserName(user.getUsername());

            dto.setSubmittedTime(record.getSubmittedTime());
            dto.setUpdatedTime(record.getUpdatedTime());
        }

        dto.setStatus(record.getStatus().toString());
        return dto;
    }
}
