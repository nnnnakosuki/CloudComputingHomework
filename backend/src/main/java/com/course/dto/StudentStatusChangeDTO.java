package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 学籍变动DTO
 */
@Data
public class StudentStatusChangeDTO {
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "变动类型不能为空")
    private Integer statusType;
    
    @NotBlank(message = "变动原因不能为空")
    private String reason;
}
