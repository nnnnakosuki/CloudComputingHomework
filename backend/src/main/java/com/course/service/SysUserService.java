package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.entity.SysUser;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名获取用户
     */
    SysUser getByUsername(String username);
    
    /**
     * 验证用户密码
     */
    boolean validatePassword(String rawPassword, String encodedPassword, PasswordEncoder passwordEncoder);
    
    /**
     * 分页查询用户
     */
    IPage<SysUser> getUserPage(IPage<SysUser> page, String keyword, Long roleId, Integer status);
}
