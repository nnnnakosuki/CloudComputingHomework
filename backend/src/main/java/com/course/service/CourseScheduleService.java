package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.CourseScheduleDTO;
import com.course.entity.CourseSchedule;
import com.course.vo.CourseScheduleVO;

import java.util.List;

/**
 * 开课计划服务接口
 */
public interface CourseScheduleService extends IService<CourseSchedule> {
    
    /**
     * 分页查询开课计划
     */
    IPage<CourseScheduleVO> getSchedulePage(IPage<CourseSchedule> page, String keyword, String semester, Long teacherId, Integer status);
    
    /**
     * 发布开课计划
     */
    boolean publishSchedule(CourseScheduleDTO scheduleDTO);
    
    /**
     * 更新开课计划
     */
    boolean updateSchedule(CourseScheduleDTO scheduleDTO);
    
    /**
     * 删除开课计划
     */
    boolean deleteSchedule(Long id);
    
    /**
     * 启用/禁用开课计划
     */
    boolean changeScheduleStatus(Long id, Integer status);
    
    /**
     * 获取可选课程列表（学生端）
     */
    List<CourseScheduleVO> getAvailableCourses(Long studentId, String semester);
    
    /**
     * 检查选课冲突
     */
    boolean checkSelectionConflict(Long studentId, Long courseScheduleId);
}
