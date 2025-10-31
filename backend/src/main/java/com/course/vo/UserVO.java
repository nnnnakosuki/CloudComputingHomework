package com.course.vo;

import lombok.Data;

/**
 * 用户视图对象
 */
@Data
public class UserVO {
    
    private Long id;
    
    private String username;
    
    private String realName;
    
    private Long roleId;
    
    private String roleCode;
    
    private String roleName;
    
    private Long deptId;
    
    private String deptName;
    
    private Integer status;
    
    private String statusText;
}
