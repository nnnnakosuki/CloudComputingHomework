package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.entity.SysUser;
import com.course.mapper.SysUserMapper;
import com.course.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }
    
    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Override
    public IPage<SysUser> getUserPage(IPage<SysUser> page, String keyword, Long roleId, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword));
        }
        
        // 角色筛选
        if (roleId != null) {
            wrapper.eq(SysUser::getRoleId, roleId);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        return page(page, wrapper);
    }
}
