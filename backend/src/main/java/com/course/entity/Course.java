package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {
    
    private String courseCode;
    
    private String courseName;
    
    private BigDecimal credits;
    
    private Integer courseType;
    
    private Long deptId;
    
    private String description;
    
    private Integer status;
}
