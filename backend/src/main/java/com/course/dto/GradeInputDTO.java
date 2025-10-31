package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 成绩录入DTO
 */
@Data
public class GradeInputDTO {
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "开课ID不能为空")
    private Long courseScheduleId;
    
    private BigDecimal score;
    
    private String remark;
}
