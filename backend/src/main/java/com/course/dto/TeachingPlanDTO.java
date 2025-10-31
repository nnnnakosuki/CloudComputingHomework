package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 教学计划DTO
 */
@Data
public class TeachingPlanDTO {
    
    private Long id;
    
    @NotBlank(message = "计划名称不能为空")
    private String planName;
    
    @NotNull(message = "院系ID不能为空")
    private Long deptId;
    
    @NotBlank(message = "专业不能为空")
    private String major;
    
    @NotBlank(message = "年级不能为空")
    private String grade;
    
    @NotNull(message = "总学分不能为空")
    private Integer totalCredits;
    
    private String description;
    
    private Integer status;
}
