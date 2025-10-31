package com.course.controller;

import com.course.common.Result;
import com.course.dto.LoginRequest;
import com.course.entity.SysUser;
import com.course.service.SysUserService;
import com.course.utils.JwtTokenUtil;
import com.course.vo.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final SysUserService sysUserService;
    private final JwtTokenUtil jwtTokenUtil;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
//            // 进行身份验证
//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                    loginRequest.getUsername(),
//                    loginRequest.getPassword()
//                )
//            );
            
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            SysUser user = sysUserService.getByUsername(loginRequest.getUsername());
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 生成JWT token
            String roleCode = getUserRoleCode(user.getRoleId());
            String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId(), roleCode);
            
            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setRoleCode(roleCode);
            userInfo.setRoleName(getUserRoleName(user.getRoleId()));
            
            response.setUserInfo(userInfo);
            
            log.info("用户 {} 登录成功", loginRequest.getUsername());
            return Result.success("登录成功", response);
            
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error("用户名或密码错误");
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    public Result<LoginResponse.UserInfo> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRoleCode(getUserRoleCode(user.getRoleId()));
        userInfo.setRoleName(getUserRoleName(user.getRoleId()));
        
        return Result.success(userInfo);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success();
    }
    
    /**
     * 根据角色ID获取角色编码
     */
    private String getUserRoleCode(Long roleId) {
        switch (roleId.intValue()) {
            case 1:
                return "admin";
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
    private String getUserRoleName(Long roleId) {
        switch (roleId.intValue()) {
            case 1:
                return "管理员";
            case 2:
                return "教务员";
            case 3:
                return "教师";
            case 4:
                return "学生";
            default:
                return "用户";
        }
    }
}
