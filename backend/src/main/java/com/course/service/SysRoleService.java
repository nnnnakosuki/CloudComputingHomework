package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.entity.SysRole;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {
    
    /**
     * 分页查询角色
     */
    IPage<SysRole> getRolePage(IPage<SysRole> page, String keyword);
}
