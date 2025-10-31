package com.example.scorequerysystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 成绩实体类，存储学生课程成绩
 */
@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的学生用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 课程名称
     */
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    /**
     * 分数，最多五位数，两位小数
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private Double score;

    /**
     * 学期，例如“2024春季”
     */
    @Column(nullable = false, length = 50)
    private String semester;

    /**
     * 录入时间
     */
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }

    // Getter 和 Setter
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
