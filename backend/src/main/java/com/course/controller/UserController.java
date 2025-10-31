package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.UserDTO;
import com.course.entity.SysUser;
import com.course.service.SysUserService;
import com.course.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 分页查询用户列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<UserVO>> getUserPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer status) {
        
        try {
            Page<SysUser> page = new Page<>(current, size);
            IPage<SysUser> userPage = sysUserService.getUserPage(page, keyword, roleId, status);
            
            // 转换为VO
            IPage<UserVO> result = userPage.convert(this::convertToVO);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败");
        }
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        try {
            SysUser user = sysUserService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            UserVO userVO = convertToVO(user);
            return Result.success(userVO);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
    
    /**
     * 添加用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            // 检查用户名是否已存在
            SysUser existingUser = sysUserService.getByUsername(userDTO.getUsername());
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }
            
            SysUser user = new SysUser();
            user.setUsername(userDTO.getUsername());
            user.setRealName(userDTO.getRealName());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRoleId(userDTO.getRoleId());
            user.setDeptId(userDTO.getDeptId());
            user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);
            
            boolean success = sysUserService.save(user);
            if (success) {
                log.info("添加用户成功: {}", userDTO.getUsername());
                return Result.success();
            } else {
                return Result.error("添加用户失败");
            }
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return Result.error("添加用户失败");
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            SysUser user = sysUserService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 检查用户名是否被其他用户使用
            SysUser existingUser = sysUserService.getByUsername(userDTO.getUsername());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                return Result.error("用户名已被其他用户使用");
            }
            
            user.setUsername(userDTO.getUsername());
            user.setRealName(userDTO.getRealName());
            user.setRoleId(userDTO.getRoleId());
            user.setDeptId(userDTO.getDeptId());
            user.setStatus(userDTO.getStatus());
            
            // 如果提供了新密码，则更新密码
            if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            
            boolean success = sysUserService.updateById(user);
            if (success) {
                log.info("更新用户成功: {}", userDTO.getUsername());
                return Result.success();
            } else {
                return Result.error("更新用户失败");
            }
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error("更新用户失败");
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            SysUser user = sysUserService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            boolean success = sysUserService.removeById(id);
            if (success) {
                log.info("删除用户成功: {}", user.getUsername());
                return Result.success();
            } else {
                return Result.error("删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("删除用户失败");
        }
    }
    
    /**
     * 获取所有用户列表（不分页）
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserVO>> getAllUsers() {
        try {
            List<SysUser> users = sysUserService.list();
            List<UserVO> userVOs = users.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            
            return Result.success(userVOs);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败");
        }
    }
    
    /**
     * 转换为VO对象
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRoleId(user.getRoleId());
        vo.setRoleCode(getRoleCode(user.getRoleId()));
        vo.setRoleName(getRoleName(user.getRoleId()));
        vo.setDeptId(user.getDeptId());
        vo.setStatus(user.getStatus());
        vo.setStatusText(user.getStatus() == 1 ? "正常" : "禁用");
        return vo;
    }
    
    /**
     * 根据角色ID获取角色编码
     */
    private String getRoleCode(Long roleId) {
        if (roleId == null) return "user";
        switch (roleId.intValue()) {
            case 1:
            case 2:
                return "admin";
            case 3:
                return "teacher";
            case 4:
                return "student";
            default:
                return "user";
        }
    }
    
    /**
     * 根据角色ID获取角色名称
     */
    private String getRoleName(Long roleId) {
        if (roleId == null) return "普通用户";
        switch (roleId.intValue()) {
            case 1:
            case 2:
                return "管理员";
            case 3:
                return "教师";
            case 4:
                return "学生";
            default:
                return "普通用户";
        }
    }
}
