package com.course.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程信息VO
 */
@Data
public class CourseVO {
    
    private Long id;
    
    private String courseCode;
    
    private String courseName;
    
    private BigDecimal credits;
    
    private Integer courseType;
    
    private String courseTypeText;
    
    private Long deptId;
    
    private String deptName;
    
    private String description;
    
    private Integer status;
    
    private String statusText;
    
    private LocalDateTime createTime;
}
