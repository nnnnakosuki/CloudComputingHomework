package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * 排课DTO
 */
@Data
public class ClassScheduleDTO {
    
    private Long id;
    
    @NotNull(message = "开课ID不能为空")
    private Long courseScheduleId;
    
    @NotNull(message = "星期几不能为空")
    private Integer weekDay;
    
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;
    
    private String classroom;
    
    private String weeks;
}
