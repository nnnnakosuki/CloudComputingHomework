package com.course.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩信息VO
 */
@Data
public class CourseGradeVO {
    
    private Long id;
    
    private Long studentId;
    
    private String studentNo;
    
    private String studentName;
    
    private Long courseScheduleId;
    
    private String courseCode;
    
    private String courseName;
    
    private BigDecimal score;
    
    private Long teacherId;
    
    private String teacherName;
    
    private LocalDateTime submitTime;
    
    private Integer auditStatus;
    
    private String auditStatusText;
    
    private Long auditorId;
    
    private String auditorName;
    
    private LocalDateTime auditTime;
    
    private LocalDateTime queryStartTime;
    
    private LocalDateTime queryEndTime;
    
    private BigDecimal credits;
    
    private Integer courseType;
}
