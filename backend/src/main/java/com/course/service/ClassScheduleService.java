package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.ClassScheduleDTO;
import com.course.entity.ClassSchedule;
import com.course.vo.ClassScheduleVO;

import java.time.LocalTime;
import java.util.List;

/**
 * 排课服务接口
 */
public interface ClassScheduleService extends IService<ClassSchedule> {
    
    /**
     * 分页查询排课信息
     */
    IPage<ClassScheduleVO> getSchedulePage(IPage<ClassSchedule> page, String keyword, String semester, Long teacherId, Integer weekDay);
    
    /**
     * 添加排课
     */
    boolean addSchedule(ClassScheduleDTO scheduleDTO);
    
    /**
     * 更新排课
     */
    boolean updateSchedule(ClassScheduleDTO scheduleDTO);
    
    /**
     * 删除排课
     */
    boolean deleteSchedule(Long id);
    
    /**
     * 批量排课
     */
    boolean batchAddSchedules(List<ClassScheduleDTO> scheduleList);
    
    /**
     * 检查时间冲突
     */
    boolean checkTimeConflict(Long courseScheduleId, Integer weekDay, LocalTime startTime, LocalTime endTime, String classroom);
    
    /**
     * 获取课表（按周）
     */
    List<ClassScheduleVO> getWeeklySchedule(String semester, Integer weekDay);
    
    /**
     * 获取教师课表
     */
    List<ClassScheduleVO> getTeacherSchedule(Long teacherId, String semester);
    
    /**
     * 获取学生课表
     */
    List<ClassScheduleVO> getStudentSchedule(Long studentId, String semester);
}
