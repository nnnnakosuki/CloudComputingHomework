package com.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学生选课管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.course.mapper")
public class CourseSelectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseSelectionApplication.class, args);
    }
}
