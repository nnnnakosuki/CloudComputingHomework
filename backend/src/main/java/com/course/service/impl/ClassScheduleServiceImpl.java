package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.ClassScheduleDTO;
import com.course.entity.ClassSchedule;
import com.course.entity.CourseSchedule;
import com.course.entity.SysUser;
import com.course.exception.BusinessException;
import com.course.mapper.ClassScheduleMapper;
import com.course.entity.CourseSelection;
import com.course.mapper.CourseSelectionMapper;
import com.course.service.ClassScheduleService;
import com.course.service.CourseScheduleService;
import com.course.service.CourseService;
import com.course.service.SysUserService;
import com.course.vo.ClassScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排课服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassScheduleServiceImpl extends ServiceImpl<ClassScheduleMapper, ClassSchedule> implements ClassScheduleService {
    
    private final CourseScheduleService courseScheduleService;
    private final SysUserService sysUserService;
    private final CourseService courseService;
    private final CourseSelectionMapper courseSelectionMapper;
    
    @Override
    public IPage<ClassScheduleVO> getSchedulePage(IPage<ClassSchedule> page, String keyword, String semester, Long teacherId, Integer weekDay) {
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            // 这里应该关联课程表查询，暂时简化处理
            wrapper.like(ClassSchedule::getId, keyword);
        }
        
        if (weekDay != null) {
            wrapper.eq(ClassSchedule::getWeekDay, weekDay);
        }
        
        wrapper.orderByAsc(ClassSchedule::getWeekDay, ClassSchedule::getStartTime);
        
        IPage<ClassSchedule> schedulePage = page(page, wrapper);
        
        // 转换为VO
        IPage<ClassScheduleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ClassScheduleVO> voList = schedulePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSchedule(ClassScheduleDTO scheduleDTO) {
        // 检查开课计划是否存在
        CourseSchedule courseSchedule = courseScheduleService.getById(scheduleDTO.getCourseScheduleId());
        if (courseSchedule == null) {
            throw new BusinessException("开课计划不存在");
        }
        
        // 检查时间冲突
        if (checkTimeConflict(scheduleDTO.getCourseScheduleId(), scheduleDTO.getWeekDay(), 
                scheduleDTO.getStartTime(), scheduleDTO.getEndTime(), scheduleDTO.getClassroom())) {
            throw new BusinessException("时间或教室冲突");
        }
        
        ClassSchedule schedule = new ClassSchedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        
        boolean saved = save(schedule);
        if (!saved) {
            throw new BusinessException("添加排课失败");
        }
        
        log.info("成功添加排课: 开课ID={}, 星期={}, 时间={}-{}", 
                scheduleDTO.getCourseScheduleId(), scheduleDTO.getWeekDay(), 
                scheduleDTO.getStartTime(), scheduleDTO.getEndTime());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchedule(ClassScheduleDTO scheduleDTO) {
        ClassSchedule existSchedule = getById(scheduleDTO.getId());
        if (existSchedule == null) {
            throw new BusinessException("排课记录不存在");
        }
        
        // 检查时间冲突（排除自己）
        if (checkTimeConflict(scheduleDTO.getCourseScheduleId(), scheduleDTO.getWeekDay(), 
                scheduleDTO.getStartTime(), scheduleDTO.getEndTime(), scheduleDTO.getClassroom(), scheduleDTO.getId())) {
            throw new BusinessException("时间或教室冲突");
        }
        
        ClassSchedule schedule = new ClassSchedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        
        boolean updated = updateById(schedule);
        if (!updated) {
            throw new BusinessException("更新排课失败");
        }
        
        log.info("成功更新排课: ID={}", scheduleDTO.getId());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(Long id) {
        ClassSchedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException("排课记录不存在");
        }
        
        boolean deleted = removeById(id);
        if (!deleted) {
            throw new BusinessException("删除排课失败");
        }
        
        log.info("成功删除排课: ID={}", id);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAddSchedules(List<ClassScheduleDTO> scheduleList) {
        for (ClassScheduleDTO scheduleDTO : scheduleList) {
            addSchedule(scheduleDTO);
        }
        
        log.info("成功批量添加排课: 数量={}", scheduleList.size());
        return true;
    }
    
    @Override
    public boolean checkTimeConflict(Long courseScheduleId, Integer weekDay, LocalTime startTime, LocalTime endTime, String classroom) {
        return checkTimeConflict(courseScheduleId, weekDay, startTime, endTime, classroom, null);
    }
    
    public boolean checkTimeConflict(Long courseScheduleId, Integer weekDay, LocalTime startTime, LocalTime endTime, String classroom, Long excludeId) {
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassSchedule::getWeekDay, weekDay)
                .eq(ClassSchedule::getClassroom, classroom)
                .and(w -> w.between(ClassSchedule::getStartTime, startTime, endTime)
                        .or().between(ClassSchedule::getEndTime, startTime, endTime)
                        .or().and(sub -> sub.le(ClassSchedule::getStartTime, startTime)
                                .ge(ClassSchedule::getEndTime, endTime)));
        
        if (excludeId != null) {
            wrapper.ne(ClassSchedule::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }
    
    @Override
    public List<ClassScheduleVO> getWeeklySchedule(String semester, Integer weekDay) {
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        if (weekDay != null) {
            wrapper.eq(ClassSchedule::getWeekDay, weekDay);
        }
        wrapper.orderByAsc(ClassSchedule::getStartTime);
        
        List<ClassSchedule> schedules = list(wrapper);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public List<ClassScheduleVO> getTeacherSchedule(Long teacherId, String semester) {
        // 通过开课计划查询该教师的课程
        LambdaQueryWrapper<CourseSchedule> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(CourseSchedule::getTeacherId, teacherId);
        if (semester != null && !semester.isEmpty()) {
            courseWrapper.eq(CourseSchedule::getSemester, semester);
        }
        List<CourseSchedule> courseSchedules = courseScheduleService.list(courseWrapper);
        
        if (courseSchedules.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        // 获取这些课程的所有排课记录
        List<Long> courseScheduleIds = courseSchedules.stream()
                .map(CourseSchedule::getId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ClassSchedule::getCourseScheduleId, courseScheduleIds)
                .orderByAsc(ClassSchedule::getWeekDay, ClassSchedule::getStartTime);
        
        List<ClassSchedule> schedules = list(wrapper);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public List<ClassScheduleVO> getStudentSchedule(Long studentId, String semester) {
        // 通过选课记录查询学生已确认的课程
        LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
        selectionWrapper.eq(CourseSelection::getStudentId, studentId)
                .eq(CourseSelection::getStatus, 2); // 已确认的选课
        
        List<CourseSelection> selections = courseSelectionMapper.selectList(selectionWrapper);
        
        if (selections.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        // 获取这些课程的所有排课记录
        List<Long> courseScheduleIds = selections.stream()
                .map(CourseSelection::getCourseScheduleId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<ClassSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ClassSchedule::getCourseScheduleId, courseScheduleIds)
                .orderByAsc(ClassSchedule::getWeekDay, ClassSchedule::getStartTime);
        
        List<ClassSchedule> schedules = list(wrapper);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    /**
     * 转换为VO
     */
    private ClassScheduleVO convertToVO(ClassSchedule schedule) {
        ClassScheduleVO vo = new ClassScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        
        // 获取开课计划信息
        CourseSchedule courseSchedule = courseScheduleService.getById(schedule.getCourseScheduleId());
        if (courseSchedule != null) {
            vo.setSemester(courseSchedule.getSemester());
            vo.setMaxStudents(courseSchedule.getMaxStudents());
            vo.setCurrentStudents(courseSchedule.getCurrentStudents());
            
            // 获取课程信息
            if (courseSchedule.getCourseId() != null) {
                com.course.entity.Course course = courseService.getById(courseSchedule.getCourseId());
                if (course != null) {
                    vo.setCourseCode(course.getCourseCode());
                    vo.setCourseName(course.getCourseName());
                }
            }
            
            // 获取教师信息
            SysUser teacher = sysUserService.getById(courseSchedule.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getRealName());
            }
        }
        
        // 设置星期文本
        String[] weekDays = {"", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        if (schedule.getWeekDay() >= 1 && schedule.getWeekDay() <= 7) {
            vo.setWeekDayText(weekDays[schedule.getWeekDay()]);
        }
        
        return vo;
    }
}
