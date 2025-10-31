package com.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调课记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("schedule_adjustment")
public class ScheduleAdjustment extends BaseEntity {
    
    private Long classScheduleId;
    
    private String oldInfo;
    
    private String newInfo;
    
    private String reason;
    
    private Long operatorId;
    
    private Integer status;
}
