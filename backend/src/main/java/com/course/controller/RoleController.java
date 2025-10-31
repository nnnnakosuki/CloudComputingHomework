package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.RoleDTO;
import com.course.entity.SysRole;
import com.course.service.SysRoleService;
import com.course.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    
    private final SysRoleService sysRoleService;
    
    /**
     * 分页查询角色列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<RoleVO>> getRolePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        
        try {
            Page<SysRole> page = new Page<>(current, size);
            IPage<SysRole> rolePage = sysRoleService.getRolePage(page, keyword);
            
            // 转换为VO
            IPage<RoleVO> result = rolePage.convert(this::convertToVO);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取角色列表失败", e);
            return Result.error("获取角色列表失败");
        }
    }
    
    /**
     * 根据ID获取角色信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<RoleVO> getRoleById(@PathVariable Long id) {
        try {
            SysRole role = sysRoleService.getById(id);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            RoleVO roleVO = convertToVO(role);
            return Result.success(roleVO);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            return Result.error("获取角色信息失败");
        }
    }
    
    /**
     * 添加角色
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addRole(@Valid @RequestBody RoleDTO roleDTO) {
        try {
            // 检查角色代码是否已存在
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, roleDTO.getRoleCode());
            SysRole existingRole = sysRoleService.getOne(wrapper);
            if (existingRole != null) {
                return Result.error("角色代码已存在");
            }
            
            SysRole role = new SysRole();
            role.setRoleName(roleDTO.getRoleName());
            role.setRoleCode(roleDTO.getRoleCode());
            role.setDescription(roleDTO.getDescription());
            
            boolean success = sysRoleService.save(role);
            if (success) {
                log.info("添加角色成功: {}", roleDTO.getRoleCode());
                return Result.success();
            } else {
                return Result.error("添加角色失败");
            }
        } catch (Exception e) {
            log.error("添加角色失败", e);
            return Result.error("添加角色失败");
        }
    }
    
    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        try {
            SysRole role = sysRoleService.getById(id);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            // 检查角色代码是否被其他角色使用
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, roleDTO.getRoleCode());
            wrapper.ne(SysRole::getId, id);
            SysRole existingRole = sysRoleService.getOne(wrapper);
            if (existingRole != null) {
                return Result.error("角色代码已被其他角色使用");
            }
            
            role.setRoleName(roleDTO.getRoleName());
            role.setRoleCode(roleDTO.getRoleCode());
            role.setDescription(roleDTO.getDescription());
            
            boolean success = sysRoleService.updateById(role);
            if (success) {
                log.info("更新角色成功: {}", roleDTO.getRoleCode());
                return Result.success();
            } else {
                return Result.error("更新角色失败");
            }
        } catch (Exception e) {
            log.error("更新角色失败", e);
            return Result.error("更新角色失败");
        }
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        try {
            SysRole role = sysRoleService.getById(id);
            if (role == null) {
                return Result.error("角色不存在");
            }
            
            // 检查是否有用户使用该角色
            // 这里可以添加检查逻辑
            
            boolean success = sysRoleService.removeById(id);
            if (success) {
                log.info("删除角色成功: {}", role.getRoleCode());
                return Result.success();
            } else {
                return Result.error("删除角色失败");
            }
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return Result.error("删除角色失败");
        }
    }
    
    /**
     * 获取所有角色列表（不分页）
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<RoleVO>> getAllRoles() {
        try {
            List<SysRole> roles = sysRoleService.list();
            List<RoleVO> roleVOs = roles.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            
            return Result.success(roleVOs);
        } catch (Exception e) {
            log.error("获取角色列表失败", e);
            return Result.error("获取角色列表失败");
        }
    }
    
    /**
     * 转换为VO对象
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        return vo;
    }
}
