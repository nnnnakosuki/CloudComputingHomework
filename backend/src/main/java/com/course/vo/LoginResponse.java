package com.course.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginResponse {
    
    private String token;
    
    private String tokenType = "Bearer";
    
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String roleCode;
        private String roleName;
    }
}
