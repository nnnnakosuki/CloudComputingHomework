package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 课程信息DTO
 */
@Data
public class CourseDTO {
    
    private Long id;
    
    @NotBlank(message = "课程编号不能为空")
    private String courseCode;
    
    @NotBlank(message = "课程名称不能为空")
    private String courseName;
    
    @NotNull(message = "学分不能为空")
    private BigDecimal credits;
    
    @NotNull(message = "课程类型不能为空")
    private Integer courseType;
    
    private Long deptId;
    
    private String description;
    
    private Integer status;
}
