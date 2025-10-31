package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.entity.SysRole;
import com.course.mapper.SysRoleMapper;
import com.course.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 角色服务实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    
    @Override
    public IPage<SysRole> getRolePage(IPage<SysRole> page, String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleCode, keyword)
                    .or().like(SysRole::getDescription, keyword));
        }
        
        wrapper.orderByDesc(SysRole::getCreateTime);
        
        return page(page, wrapper);
    }
}
