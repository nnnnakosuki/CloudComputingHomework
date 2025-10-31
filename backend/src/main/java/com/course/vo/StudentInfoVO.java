package com.course.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生信息VO
 */
@Data
public class StudentInfoVO {
    
    private Long id;
    
    private String studentNo;
    
    private String name;
    
    private Integer gender;
    
    private String genderText;
    
    private Long deptId;
    
    private String deptName;
    
    private String major;
    
    private String className;
    
    private String enrollmentYear;
    
    private Integer status;
    
    private String statusText;
    
    private String username;
    
    private String realName;
    
    private LocalDateTime createTime;
}
