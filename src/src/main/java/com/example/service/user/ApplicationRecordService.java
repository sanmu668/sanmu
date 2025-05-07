package com.example.service.user;

import com.example.dto.ApplicationRecordDTO;
import com.example.entity.ApplicationRecord;
import com.example.entity.Job;
import com.example.entity.User;
import com.example.entity.resume.Resume;
import com.example.repository.ApplicationRecordRepository;
import com.example.repository.JobRepository;
import com.example.repository.ResumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wzs
 * @date 2025年05月05日 11:49
 */
@Service
@RequiredArgsConstructor
public class ApplicationRecordService {
    private final ApplicationRecordRepository applicationRecordRepository;
    private final JobRepository jobRepository;
    @Transactional
    public ApplicationRecordDTO submitApplication(Integer userId,Integer jobId,Integer resumeId){
        if (applicationRecordRepository.existsByUserIdAndJobId(userId,jobId)){
            throw new RuntimeException("已提交过申请");
        }

        //创建提交记录
        ApplicationRecord record = new ApplicationRecord();
        record.setUserId(userId);
        record.setJobId(jobId);
        record.setResumeId(resumeId);
        record.setStatus(ApplicationRecord.ApplicationStatus.待处理);

        //保存提交记录
        ApplicationRecord saveRecord = applicationRecordRepository.save(record);

        return convertToDTO(saveRecord);
    }

    public List<ApplicationRecordDTO> getUserApplications(Integer userId){
        return applicationRecordRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ApplicationRecordDTO convertToDTO(ApplicationRecord record) {
        ApplicationRecordDTO dto = new ApplicationRecordDTO();
        BeanUtils.copyProperties(record,dto);

        Job job = jobRepository.findById(record.getJobId()).orElse(null);

        if (job != null){
            dto.setJobName(job.getJobName());
            dto.setSalary_range(job.getSalaryRange());
            dto.setCompany(job.getCompany());
        }

        dto.setStatus(record.getStatus().toString());

        return dto;
    }
}
