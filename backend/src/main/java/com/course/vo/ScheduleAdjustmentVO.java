package com.course.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调课记录VO
 */
@Data
public class ScheduleAdjustmentVO {
    
    private Long id;
    
    private Long classScheduleId;
    
    private String courseName;
    
    private String teacherName;
    
    private String oldInfo;
    
    private String newInfo;
    
    private String reason;
    
    private Long operatorId;
    
    private String operatorName;
    
    private Integer status;
    
    private String statusText;
    
    private LocalDateTime createTime;
}
