package com.course.security;

import com.course.entity.SysUser;
import com.course.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security用户详情服务
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final SysUserService sysUserService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        // 根据角色设置权限
        String roleCode = getUserRoleCode(user.getRoleId());
        
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleCode)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getStatus() == 0)
                .build();
    }
    
    /**
     * 根据角色ID获取角色编码
     */
    private String getUserRoleCode(Long roleId) {
        // 这里应该查询角色表获取角色编码，暂时硬编码
        switch (roleId.intValue()) {
            case 1:
                return "ADMIN";
            case 2:
                return "ADMIN";
            case 3:
                return "TEACHER";
            case 4:
                return "STUDENT";
            default:
                return "USER";
        }
    }
}
