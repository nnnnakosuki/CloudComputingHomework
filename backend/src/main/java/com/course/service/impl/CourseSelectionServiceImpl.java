package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.CourseSelectionDTO;
import com.course.entity.CourseSchedule;
import com.course.entity.CourseSelection;
import com.course.entity.StudentInfo;
import com.course.entity.SysUser;
import com.course.exception.BusinessException;
import com.course.mapper.CourseSelectionMapper;
import com.course.service.CourseScheduleService;
import com.course.service.CourseSelectionService;
import com.course.service.CourseService;
import com.course.service.StudentInfoService;
import com.course.service.SysUserService;
import com.course.vo.CourseSelectionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 选课服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSelectionServiceImpl extends ServiceImpl<CourseSelectionMapper, CourseSelection> implements CourseSelectionService {
    
    private final CourseScheduleService courseScheduleService;
    private final StudentInfoService studentInfoService;
    private final SysUserService sysUserService;
    private final CourseService courseService;
    
    @Override
    public IPage<CourseSelectionVO> getSelectionPage(IPage<CourseSelection> page, Long studentId, Long courseScheduleId, Integer status) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        
        if (studentId != null) {
            wrapper.eq(CourseSelection::getStudentId, studentId);
        }
        
        if (courseScheduleId != null) {
            wrapper.eq(CourseSelection::getCourseScheduleId, courseScheduleId);
        }
        
        if (status != null) {
            wrapper.eq(CourseSelection::getStatus, status);
        }
        
        wrapper.orderByDesc(CourseSelection::getCreateTime);
        
        IPage<CourseSelection> selectionPage = page(page, wrapper);
        
        // 转换为VO
        IPage<CourseSelectionVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<CourseSelectionVO> voList = selectionPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean selectCourse(CourseSelectionDTO selectionDTO) {
        // 检查学生是否存在
        StudentInfo student = studentInfoService.getById(selectionDTO.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 检查开课计划是否存在且开放选课
        CourseSchedule schedule = courseScheduleService.getById(selectionDTO.getCourseScheduleId());
        if (schedule == null) {
            throw new BusinessException("开课计划不存在");
        }
        
        if (schedule.getStatus() != 1) {
            throw new BusinessException("该课程未开放选课");
        }
        
        // 检查是否已选该课程
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, selectionDTO.getStudentId())
                .eq(CourseSelection::getCourseScheduleId, selectionDTO.getCourseScheduleId());
        
        if (count(wrapper) > 0) {
            throw new BusinessException("您已选择该课程");
        }
        
        // 检查选课容量
        if (schedule.getCurrentStudents() >= schedule.getMaxStudents()) {
            throw new BusinessException("该课程选课人数已满");
        }
        
        // 检查选课冲突（时间冲突）
        if (courseScheduleService.checkSelectionConflict(selectionDTO.getStudentId(), selectionDTO.getCourseScheduleId())) {
            throw new BusinessException("选课时间冲突");
        }
        
        // 创建选课记录
        CourseSelection selection = new CourseSelection();
        BeanUtils.copyProperties(selectionDTO, selection);
        selection.setSelectionType(1); // 正常选课
        selection.setStatus(1); // 待确认
        selection.setSelectTime(LocalDateTime.now());
        
        boolean saved = save(selection);
        if (!saved) {
            throw new BusinessException("选课失败");
        }
        
        // 更新开课计划的当前选课人数
        schedule.setCurrentStudents(schedule.getCurrentStudents() + 1);
        courseScheduleService.updateById(schedule);
        
        log.info("学生选课成功: 学生ID={}, 开课ID={}", selectionDTO.getStudentId(), selectionDTO.getCourseScheduleId());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dropCourse(Long selectionId) {
        CourseSelection selection = getById(selectionId);
        if (selection == null) {
            throw new BusinessException("选课记录不存在");
        }
        
        if (selection.getStatus() == 3) {
            throw new BusinessException("该课程已退课");
        }
        
        // 更新选课状态为已退课
        selection.setStatus(3);
        boolean updated = updateById(selection);
        if (!updated) {
            throw new BusinessException("退课失败");
        }
        
        // 更新开课计划的当前选课人数
        CourseSchedule schedule = courseScheduleService.getById(selection.getCourseScheduleId());
        if (schedule != null) {
            schedule.setCurrentStudents(Math.max(0, schedule.getCurrentStudents() - 1));
            courseScheduleService.updateById(schedule);
        }
        
        log.info("学生退课成功: 选课ID={}", selectionId);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchConfirmSelections(List<Long> selectionIds) {
        for (Long selectionId : selectionIds) {
            CourseSelection selection = getById(selectionId);
            if (selection != null && selection.getStatus() == 1) {
                selection.setStatus(2); // 已确认
                selection.setConfirmTime(LocalDateTime.now());
                updateById(selection);
            }
        }
        
        log.info("批量确认选课成功: 数量={}", selectionIds.size());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRejectSelections(List<Long> selectionIds) {
        for (Long selectionId : selectionIds) {
            CourseSelection selection = getById(selectionId);
            if (selection != null && selection.getStatus() == 1) {
                selection.setStatus(3); // 已拒绝
                selection.setConfirmTime(LocalDateTime.now());
                updateById(selection);
                
                // 更新开课计划的当前选课人数
                CourseSchedule schedule = courseScheduleService.getById(selection.getCourseScheduleId());
                if (schedule != null) {
                    schedule.setCurrentStudents(Math.max(0, schedule.getCurrentStudents() - 1));
                    courseScheduleService.updateById(schedule);
                }
            }
        }
        
        log.info("批量拒绝选课成功: 数量={}", selectionIds.size());
        return true;
    }
    
    @Override
    public List<CourseSelectionVO> getStudentSelections(Long studentId, String semester) {
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getStudentId, studentId);
        
        List<CourseSelection> selections = list(wrapper);
        return selections.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public boolean checkCourseCapacity(Long courseScheduleId) {
        CourseSchedule schedule = courseScheduleService.getById(courseScheduleId);
        if (schedule == null) {
            return false;
        }
        
        return schedule.getCurrentStudents() < schedule.getMaxStudents();
    }
    
    /**
     * 转换为VO
     */
    private CourseSelectionVO convertToVO(CourseSelection selection) {
        CourseSelectionVO vo = new CourseSelectionVO();
        BeanUtils.copyProperties(selection, vo);
        
        // 获取学生信息
        StudentInfo student = studentInfoService.getById(selection.getStudentId());
        if (student != null) {
            vo.setStudentNo(student.getStudentNo());
            vo.setStudentName(student.getName());
        }
        
        // 获取开课计划信息
        CourseSchedule schedule = courseScheduleService.getById(selection.getCourseScheduleId());
        if (schedule != null) {
            vo.setSemester(schedule.getSemester());
            
            // 获取课程信息
            if (schedule.getCourseId() != null) {
                com.course.entity.Course course = courseService.getById(schedule.getCourseId());
                if (course != null) {
                    vo.setCourseCode(course.getCourseCode());
                    vo.setCourseName(course.getCourseName());
                    vo.setCredits(course.getCredits());
                }
            }
            
            // 获取教师信息
            SysUser teacher = sysUserService.getById(schedule.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getRealName());
            }
        }
        
        // 设置选课类型文本
        vo.setSelectionTypeText(selection.getSelectionType() == 1 ? "正常选课" : "补选");
        
        // 设置状态文本
        switch (selection.getStatus()) {
            case 1:
                vo.setStatusText("待确认");
                break;
            case 2:
                vo.setStatusText("已确认");
                break;
            case 3:
                vo.setStatusText("已退课");
                break;
            default:
                vo.setStatusText("未知");
        }
        
        return vo;
    }
}
