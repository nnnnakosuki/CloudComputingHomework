package com.course.vo;

import lombok.Data;

import java.time.LocalTime;

/**
 * 排课VO
 */
@Data
public class ClassScheduleVO {
    
    private Long id;
    
    private Long courseScheduleId;
    
    private String courseCode;
    
    private String courseName;
    
    private String teacherName;
    
    private String semester;
    
    private Integer weekDay;
    
    private String weekDayText;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private String classroom;
    
    private String weeks;
    
    private Integer maxStudents;
    
    private Integer currentStudents;
}
