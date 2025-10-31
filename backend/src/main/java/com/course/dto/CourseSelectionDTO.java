package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 选课DTO
 */
@Data
public class CourseSelectionDTO {
    
    private Long id;
    
    private Long studentId; // 不再必填，后端自动设置
    
    @NotNull(message = "开课ID不能为空")
    private Long courseScheduleId;
    
    private Integer selectionType;
    
    private Integer status;
}
