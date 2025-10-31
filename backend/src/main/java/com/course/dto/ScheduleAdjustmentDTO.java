package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 调课申请DTO
 */
@Data
public class ScheduleAdjustmentDTO {
    
    private Long id;
    
    @NotNull(message = "排课ID不能为空")
    private Long classScheduleId;
    
    @NotBlank(message = "调课原因不能为空")
    private String reason;
    
    private String newInfo;
}
