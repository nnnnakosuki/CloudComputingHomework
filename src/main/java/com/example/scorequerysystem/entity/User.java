package com.example.scorequerysystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类，记录学生、教师和管理员信息
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 登录用户名，唯一
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码，已加密存储
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 用户角色：STUDENT 学生，TEACHER 教师，ADMIN 管理员
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    /**
     * 学号，仅学生填写
     */
    @Column(length = 20)
    private String studentId;

    /**
     * 教师工号，仅教师填写
     */
    @Column(length = 20)
    private String teacherId;

    /**
     * 姓名
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 班级
     */
    @Column(length = 50)
    private String className;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 枚举类型定义
    public enum Role {
        STUDENT,
        TEACHER,
        ADMIN
    }

    // Getter和Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
