package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 角色数据传输对象
 */
@Data
public class RoleDTO {
    
    private Long id;
    
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    
    @NotBlank(message = "角色代码不能为空")
    private String roleCode;
    
    private String description;
}
