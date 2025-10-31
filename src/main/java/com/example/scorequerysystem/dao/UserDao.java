package com.example.scorequerysystem.dao;

import com.example.scorequerysystem.entity.User;

import java.sql.*;

/**
 * User数据访问对象，数据库中User表的增删查改操作
 */
public class UserDao {

    private Connection getConnection() throws SQLException {
        // TODO 请根据你的数据库配置修改连接字符串、用户名和密码
        String url = "jdbc:mysql://localhost:3306/scoredb?useSSL=false&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "123456";
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * 根据用户名查询用户，返回User实体
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, username);
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     User user = new User();
                     user.setId(rs.getLong("id"));
                     user.setUsername(rs.getString("username"));
                     user.setPassword(rs.getString("password"));
                     user.setRole(User.Role.valueOf(rs.getString("role")));
                     user.setStudentId(rs.getString("student_id"));
                     user.setTeacherId(rs.getString("teacher_id"));
                     user.setName(rs.getString("name"));
                     user.setClassName(rs.getString("class"));
                     return user;
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO 后续可添加用户注册、更新等操作
}
