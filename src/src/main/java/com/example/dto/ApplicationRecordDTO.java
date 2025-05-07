package com.example.dto;

/**
 * @author wzs
 * @date 2025年05月05日 11:55
 */
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationRecordDTO {
    private Integer id;
    private Integer resumeId;
    private Integer jobId;
    private Integer userId;
    private String userName;
    private String jobName;
    private String company;
    private String salary_range;
    private String status;
    private LocalDateTime submittedTime;
    private LocalDateTime updatedTime;
}
