package com.course.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private String password;
    
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    
    private Long deptId;
    
    private Integer status;
}
