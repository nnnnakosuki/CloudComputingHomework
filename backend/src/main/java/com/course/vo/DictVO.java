package com.course.vo;

import lombok.Data;

/**
 * 数据字典视图对象
 */
@Data
public class DictVO {
    
    private Long id;
    
    private String dictType;
    
    private String dictLabel;
    
    private String dictValue;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private String statusText;
    
    private String remark;
}
