package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_info")
public class StudentInfo extends BaseEntity {
    
    private Long userId;
    
    private String studentNo;
    
    private String name;
    
    private Integer gender;
    
    private Long deptId;
    
    private String major;
    
    private String className;
    
    private String enrollmentYear;
    
    private Integer status;
}
