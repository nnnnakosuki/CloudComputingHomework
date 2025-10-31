package com.example.scorequerysystem.service;

import com.example.scorequerysystem.dao.UserDao;
import com.example.scorequerysystem.entity.User;

/**
 * 用户业务逻辑服务类
 */
public class UserService {

    private UserDao userDao = new UserDao();

    /**
     * 根据用户名和密码进行用户身份验证
     * @param username 用户名
     * @param password 明文密码（后续可改为加密比对）
     * @return 验证成功返回User对象，失败返回null
     */
    public User authenticate(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null) {
            // 简单密码对比，后续可使用加密算法
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}

    //TODO 后续可以添加用户注册、修改密码等业务逻辑
