package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 学籍变动实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_status")
public class StudentStatus extends BaseEntity {
    
    private Long studentId;
    
    private Integer statusType;
    
    private String reason;
    
    private Long operatorId;
    
    private LocalDateTime operateTime;
}
