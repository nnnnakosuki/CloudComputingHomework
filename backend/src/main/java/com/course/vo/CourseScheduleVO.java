package com.course.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开课计划VO
 */
@Data
public class CourseScheduleVO {
    
    private Long id;
    
    private Long courseId;
    
    private String courseCode;
    
    private String courseName;
    
    private BigDecimal credits;
    
    private Integer courseType;
    
    private String courseTypeText;
    
    private String semester;
    
    private Long teacherId;
    
    private String teacherName;
    
    private Integer maxStudents;
    
    private Integer currentStudents;
    
    private Integer status;
    
    private String statusText;
    
    private LocalDateTime createTime;
}
