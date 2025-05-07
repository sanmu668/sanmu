package com.example.dto;

import lombok.Data;

/**
 * @author wzs
 * @date 2025年05月05日 0:09
 */
@Data
public class JobSearchRequest {
    private String jobName;
    private String location;
    private String experience;
    private Integer page = 1;
    private Integer size = 10;
}
