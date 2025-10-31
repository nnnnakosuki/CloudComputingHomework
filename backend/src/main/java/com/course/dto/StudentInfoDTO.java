package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 学生信息DTO
 */
@Data
public class StudentInfoDTO {
    
    private Long id;
    
    @NotBlank(message = "学号不能为空")
    private String studentNo;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotNull(message = "性别不能为空")
    private Integer gender;
    
    private Long deptId;
    
    private String major;
    
    private String className;
    
    private String enrollmentYear;
    
    private Integer status;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
}
