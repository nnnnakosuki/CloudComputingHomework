package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 排课实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class_schedule")
public class ClassSchedule extends BaseEntity {
    
    private Long courseScheduleId;
    
    private Integer weekDay;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String classroom;
    
    private String weeks;
}
