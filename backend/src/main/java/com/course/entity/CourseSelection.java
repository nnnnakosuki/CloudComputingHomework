package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 选课记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_selection")
public class CourseSelection extends BaseEntity {
    
    private Long studentId;
    
    private Long courseScheduleId;
    
    private Integer selectionType;
    
    private Integer status;
    
    private LocalDateTime selectTime;
    
    private LocalDateTime confirmTime;
}
