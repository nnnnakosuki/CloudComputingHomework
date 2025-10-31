package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教学计划实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teaching_plan")
public class TeachingPlan extends BaseEntity {
    
    private String planName;
    
    private Long deptId;
    
    private String major;
    
    private String grade;
    
    private Integer totalCredits;
    
    private String description;
    
    private Integer status;
}
