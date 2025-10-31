package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.CourseScheduleDTO;
import com.course.entity.Course;
import com.course.entity.CourseSchedule;
import com.course.entity.SysUser;
import com.course.exception.BusinessException;
import com.course.mapper.CourseScheduleMapper;
import com.course.service.CourseScheduleService;
import com.course.service.CourseService;
import com.course.service.SysUserService;
import com.course.vo.CourseScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 开课计划服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleMapper, CourseSchedule> implements CourseScheduleService {
    
    private final CourseService courseService;
    private final SysUserService sysUserService;
    
    @Override
    public IPage<CourseScheduleVO> getSchedulePage(IPage<CourseSchedule> page, String keyword, String semester, Long teacherId, Integer status) {
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            // 这里应该关联课程表查询，暂时简化处理
            wrapper.like(CourseSchedule::getId, keyword);
        }
        
        if (StringUtils.hasText(semester)) {
            wrapper.eq(CourseSchedule::getSemester, semester);
        }
        
        if (teacherId != null) {
            wrapper.eq(CourseSchedule::getTeacherId, teacherId);
        }
        
        if (status != null) {
            wrapper.eq(CourseSchedule::getStatus, status);
        }
        
        wrapper.orderByDesc(CourseSchedule::getCreateTime);
        
        IPage<CourseSchedule> schedulePage = page(page, wrapper);
        
        // 转换为VO
        IPage<CourseScheduleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<CourseScheduleVO> voList = schedulePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishSchedule(CourseScheduleDTO scheduleDTO) {
        // 检查课程是否存在
        Course course = courseService.getById(scheduleDTO.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查教师是否存在
        SysUser teacher = sysUserService.getById(scheduleDTO.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        
        // 检查同一学期同一课程是否已开课
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getCourseId, scheduleDTO.getCourseId())
                .eq(CourseSchedule::getSemester, scheduleDTO.getSemester());
        
        if (count(wrapper) > 0) {
            throw new BusinessException("该课程在当前学期已开课");
        }
        
        CourseSchedule schedule = new CourseSchedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        schedule.setCurrentStudents(0);
        schedule.setStatus(1); // 开放选课状态
        
        boolean saved = save(schedule);
        if (!saved) {
            throw new BusinessException("发布开课计划失败");
        }
        
        log.info("成功发布开课计划: 课程ID={}, 学期={}", scheduleDTO.getCourseId(), scheduleDTO.getSemester());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchedule(CourseScheduleDTO scheduleDTO) {
        CourseSchedule existSchedule = getById(scheduleDTO.getId());
        if (existSchedule == null) {
            throw new BusinessException("开课计划不存在");
        }
        
        CourseSchedule schedule = new CourseSchedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        
        boolean updated = updateById(schedule);
        if (!updated) {
            throw new BusinessException("更新开课计划失败");
        }
        
        log.info("成功更新开课计划: ID={}", scheduleDTO.getId());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(Long id) {
        CourseSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("开课计划不存在");
        }
        
        // 检查是否已有学生选课
        if (schedule.getCurrentStudents() > 0) {
            throw new BusinessException("该课程已有学生选课，无法删除");
        }
        
        boolean deleted = removeById(id);
        if (!deleted) {
            throw new BusinessException("删除开课计划失败");
        }
        
        log.info("成功删除开课计划: ID={}", id);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeScheduleStatus(Long id, Integer status) {
        CourseSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("开课计划不存在");
        }
        
        schedule.setStatus(status);
        boolean updated = updateById(schedule);
        if (!updated) {
            throw new BusinessException("更新开课计划状态失败");
        }
        
        log.info("成功更新开课计划状态: ID={}, 状态={}", id, status);
        return true;
    }
    
    @Override
    public List<CourseScheduleVO> getAvailableCourses(Long studentId, String semester) {
        LambdaQueryWrapper<CourseSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSchedule::getStatus, 1) // 开放选课
                .eq(CourseSchedule::getSemester, semester)
                .apply("current_students < max_students"); // 未满员
        
        List<CourseSchedule> schedules = list(wrapper);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public boolean checkSelectionConflict(Long studentId, Long courseScheduleId) {
        // 这里应该检查时间冲突，暂时返回false（无冲突）
        // 实际实现需要查询排课表，检查时间是否冲突
        return false;
    }
    
    /**
     * 转换为VO
     */
    private CourseScheduleVO convertToVO(CourseSchedule schedule) {
        CourseScheduleVO vo = new CourseScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        
        // 获取课程信息
        Course course = courseService.getById(schedule.getCourseId());
        if (course != null) {
            vo.setCourseCode(course.getCourseCode());
            vo.setCourseName(course.getCourseName());
            vo.setCredits(course.getCredits());
            vo.setCourseType(course.getCourseType());
            vo.setCourseTypeText(course.getCourseType() == 1 ? "必修" : "选修");
        }
        
        // 获取教师信息
        SysUser teacher = sysUserService.getById(schedule.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getRealName());
        }
        
        // 设置状态文本
        switch (schedule.getStatus()) {
            case 1:
                vo.setStatusText("开放选课");
                break;
            case 2:
                vo.setStatusText("选课结束");
                break;
            case 0:
                vo.setStatusText("取消开课");
                break;
            default:
                vo.setStatusText("未知");
        }
        
        return vo;
    }
}
