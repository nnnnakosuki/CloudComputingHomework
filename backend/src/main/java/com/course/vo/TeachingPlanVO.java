package com.course.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教学计划VO
 */
@Data
public class TeachingPlanVO {
    
    private Long id;
    
    private String planName;
    
    private Long deptId;
    
    private String deptName;
    
    private String major;
    
    private String grade;
    
    private Integer totalCredits;
    
    private String description;
    
    private Integer status;
    
    private String statusText;
    
    private LocalDateTime createTime;
}
