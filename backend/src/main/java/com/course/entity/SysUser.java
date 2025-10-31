package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private Long roleId;
    
    private Long deptId;
    
    private Integer status;
}
