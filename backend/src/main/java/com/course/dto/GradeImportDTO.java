package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 成绩导入DTO
 */
@Data
public class GradeImportDTO {
    
    @NotBlank(message = "学号不能为空")
    private String studentNo;
    
    @NotBlank(message = "学生姓名不能为空")
    private String studentName;
    
    @NotBlank(message = "课程名称不能为空")
    private String courseName;
    
    @NotNull(message = "分数不能为空")
    private BigDecimal score;
    
    @NotBlank(message = "学期不能为空")
    private String semester;
}

