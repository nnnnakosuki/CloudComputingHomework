package com.course.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课记录VO
 */
@Data
public class CourseSelectionVO {
    
    private Long id;
    
    private Long studentId;
    
    private String studentNo;
    
    private String studentName;
    
    private Long courseScheduleId;
    
    private String courseCode;
    
    private String courseName;
    
    private BigDecimal credits;
    
    private String semester;
    
    private String teacherName;
    
    private Integer selectionType;
    
    private String selectionTypeText;
    
    private Integer status;
    
    private String statusText;
    
    private LocalDateTime selectTime;
    
    private LocalDateTime confirmTime;
}
