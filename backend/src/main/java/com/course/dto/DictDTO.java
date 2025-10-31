package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 数据字典数据传输对象
 */
@Data
public class DictDTO {
    
    private Long id;
    
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
    
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;
    
    @NotBlank(message = "字典值不能为空")
    private String dictValue;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private String remark;
}
