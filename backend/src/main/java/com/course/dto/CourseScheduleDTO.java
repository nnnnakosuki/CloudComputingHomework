package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 开课计划DTO
 */
@Data
public class CourseScheduleDTO {
    
    private Long id;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "学期不能为空")
    private String semester;
    
    @NotNull(message = "任课教师ID不能为空")
    private Long teacherId;
    
    @NotNull(message = "最大选课人数不能为空")
    private Integer maxStudents;
    
    private Integer currentStudents;
    
    private Integer status;
}
