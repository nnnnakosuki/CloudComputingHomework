package com.course.vo;

import lombok.Data;

/**
 * 角色视图对象
 */
@Data
public class RoleVO {
    
    private Long id;
    
    private String roleName;
    
    private String roleCode;
    
    private String description;
}
