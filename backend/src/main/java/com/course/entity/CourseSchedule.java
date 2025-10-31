package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 开课计划实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_schedule")
public class CourseSchedule extends BaseEntity {
    
    private Long courseId;
    
    private String semester;
    
    private Long teacherId;
    
    private Integer maxStudents;
    
    private Integer currentStudents;
    
    private Integer status;
}
