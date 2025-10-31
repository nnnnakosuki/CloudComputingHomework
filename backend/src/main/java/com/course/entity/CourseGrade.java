package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_grade")
public class CourseGrade extends BaseEntity {
    
    private Long studentId;
    
    private Long courseScheduleId;
    
    private BigDecimal score;
    
    private Long teacherId;
    
    private Integer auditStatus;
    
    private Long auditorId;
    
    private LocalDateTime submitTime;
    
    private LocalDateTime auditTime;
    
    private LocalDateTime queryStartTime;
    
    private LocalDateTime queryEndTime;
}
