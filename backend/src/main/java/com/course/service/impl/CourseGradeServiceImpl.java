package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.GradeInputDTO;
import com.course.dto.GradeImportDTO;
import com.course.entity.*;
import com.course.exception.BusinessException;
import com.course.mapper.CourseGradeMapper;
import com.course.mapper.StudentInfoMapper;
import com.course.service.CourseGradeService;
import com.course.service.CourseScheduleService;
import com.course.service.CourseService;
import com.course.service.StudentInfoService;
import com.course.service.SysUserService;
import com.course.vo.CourseGradeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseGradeServiceImpl extends ServiceImpl<CourseGradeMapper, CourseGrade> implements CourseGradeService {
    
    private final SysUserService sysUserService;
    private final CourseService courseService;
    private final CourseScheduleService courseScheduleService;
    private final StudentInfoService studentInfoService;
    private final StudentInfoMapper studentInfoMapper;
    
    @Override
    public IPage<CourseGradeVO> getGradePage(IPage<CourseGrade> page, Long studentId, Long courseScheduleId, Integer auditStatus) {
        LambdaQueryWrapper<CourseGrade> wrapper = new LambdaQueryWrapper<>();
        
        if (studentId != null) {
            wrapper.eq(CourseGrade::getStudentId, studentId);
        }
        
        if (courseScheduleId != null) {
            wrapper.eq(CourseGrade::getCourseScheduleId, courseScheduleId);
        }
        
        if (auditStatus != null) {
            wrapper.eq(CourseGrade::getAuditStatus, auditStatus);
        }
        
        wrapper.orderByDesc(CourseGrade::getCreateTime);
        
        IPage<CourseGrade> gradePage = page(page, wrapper);
        
        // 转换为VO
        IPage<CourseGradeVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<CourseGradeVO> voList = gradePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inputGrade(GradeInputDTO gradeInputDTO) {
        // 检查是否已存在成绩记录
        CourseGrade existGrade = getByStudentAndCourse(gradeInputDTO.getStudentId(), gradeInputDTO.getCourseScheduleId());
        
        CourseGrade grade;
        if (existGrade != null) {
            // 更新现有成绩
            grade = existGrade;
            grade.setScore(gradeInputDTO.getScore());
            grade.setSubmitTime(LocalDateTime.now());
            grade.setAuditStatus(1); // 待审核
        } else {
            // 创建新成绩记录
            grade = new CourseGrade();
            grade.setStudentId(gradeInputDTO.getStudentId());
            grade.setCourseScheduleId(gradeInputDTO.getCourseScheduleId());
            grade.setScore(gradeInputDTO.getScore());
            grade.setTeacherId(1L); // 当前教师ID，实际应该从SecurityContext获取
            grade.setSubmitTime(LocalDateTime.now());
            grade.setAuditStatus(1); // 待审核
        }
        
        boolean saved = saveOrUpdate(grade);
        if (!saved) {
            throw new BusinessException("录入成绩失败");
        }
        
        log.info("成功录入成绩: 学生ID={}, 开课ID={}, 成绩={}", 
                gradeInputDTO.getStudentId(), gradeInputDTO.getCourseScheduleId(), gradeInputDTO.getScore());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInputGrades(Long courseScheduleId, List<GradeInputDTO> gradeList) {
        for (GradeInputDTO gradeInputDTO : gradeList) {
            gradeInputDTO.setCourseScheduleId(courseScheduleId);
            inputGrade(gradeInputDTO);
        }
        
        log.info("成功批量录入成绩: 开课ID={}, 成绩数量={}", courseScheduleId, gradeList.size());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditGrade(Long gradeId, Integer auditStatus, String remark) {
        CourseGrade grade = getById(gradeId);
        if (grade == null) {
            throw new BusinessException("成绩记录不存在");
        }
        
        grade.setAuditStatus(auditStatus);
        grade.setAuditorId(1L); // 当前审核员ID，实际应该从SecurityContext获取
        grade.setAuditTime(LocalDateTime.now());
        
        boolean updated = updateById(grade);
        if (!updated) {
            throw new BusinessException("审核成绩失败");
        }
        
        log.info("成功审核成绩: 成绩ID={}, 审核状态={}", gradeId, auditStatus);
        return true;
    }
    
    @Override
    public CourseGrade getByStudentAndCourse(Long studentId, Long courseScheduleId) {
        LambdaQueryWrapper<CourseGrade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseGrade::getStudentId, studentId)
                .eq(CourseGrade::getCourseScheduleId, courseScheduleId);
        return getOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importGrades(List<GradeImportDTO> importList) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        
        for (int i = 0; i < importList.size(); i++) {
            GradeImportDTO importDTO = importList.get(i);
            int rowNum = i + 2; // 第几行数据
            
            try {
                // 1. 验证学号
                LambdaQueryWrapper<StudentInfo> studentWrapper = new LambdaQueryWrapper<>();
                studentWrapper.eq(StudentInfo::getStudentNo, importDTO.getStudentNo());
                StudentInfo student = studentInfoMapper.selectOne(studentWrapper);
                
                if (student == null) {
                    errors.add(String.format("第%d行: 学号'%s'不存在", rowNum, importDTO.getStudentNo()));
                    continue;
                }
                
                // 2. 验证课程
                LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
                courseWrapper.eq(Course::getCourseName, importDTO.getCourseName());
                Course course = courseService.getOne(courseWrapper);
                
                if (course == null) {
                    errors.add(String.format("第%d行: 课程'%s'不存在", rowNum, importDTO.getCourseName()));
                    continue;
                }
                
                // 3. 查找对应的开课记录
                LambdaQueryWrapper<CourseSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
                scheduleWrapper.eq(CourseSchedule::getCourseId, course.getId())
                        .eq(CourseSchedule::getSemester, importDTO.getSemester());
                CourseSchedule courseSchedule = courseScheduleService.getOne(scheduleWrapper);
                
                if (courseSchedule == null) {
                    errors.add(String.format("第%d行: 学期'%s'没有开设课程'%s'", rowNum, importDTO.getSemester(), importDTO.getCourseName()));
                    continue;
                }
                
                // 4. 验证分数
                if (importDTO.getScore().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                    importDTO.getScore().compareTo(new java.math.BigDecimal("100")) > 0) {
                    errors.add(String.format("第%d行: 分数必须在0-100之间", rowNum));
                    continue;
                }
                
                // 5. 插入或更新成绩
                CourseGrade grade = getByStudentAndCourse(student.getId(), courseSchedule.getId());
                if (grade == null) {
                    grade = new CourseGrade();
                    grade.setStudentId(student.getId());
                    grade.setCourseScheduleId(courseSchedule.getId());
                    grade.setScore(importDTO.getScore());
                    grade.setTeacherId(courseSchedule.getTeacherId());
                    grade.setSubmitTime(LocalDateTime.now());
                    grade.setAuditStatus(1); // 待审核
                    save(grade);
                } else {
                    grade.setScore(importDTO.getScore());
                    grade.setSubmitTime(LocalDateTime.now());
                    updateById(grade);
                }
                
                successCount++;
            } catch (Exception e) {
                errors.add(String.format("第%d行: %s", rowNum, e.getMessage()));
            }
        }
        
        log.info("成绩导入完成: 成功={}, 失败={}", successCount, errors.size());
        
        if (!errors.isEmpty()) {
            throw new BusinessException("部分数据导入失败: " + String.join("; ", errors));
        }
        
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setQueryTime(Long courseScheduleId, LocalDateTime startTime, LocalDateTime endTime) {
        // 更新该开课下所有成绩的查询时段
        LambdaUpdateWrapper<CourseGrade> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CourseGrade::getCourseScheduleId, courseScheduleId)
                .set(CourseGrade::getQueryStartTime, startTime)
                .set(CourseGrade::getQueryEndTime, endTime);
        
        return update(updateWrapper);
    }
    
    @Override
    public boolean checkQueryTime(CourseGrade grade) {
        if (grade.getQueryStartTime() == null || grade.getQueryEndTime() == null) {
            // 未设置查询时段，默认允许查询
            return true;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(grade.getQueryStartTime()) && now.isBefore(grade.getQueryEndTime());
    }
    
    /**
     * 转换为VO
     */
    private CourseGradeVO convertToVO(CourseGrade grade) {
        CourseGradeVO vo = new CourseGradeVO();
        BeanUtils.copyProperties(grade, vo);
        
        // 设置审核状态文本
        switch (grade.getAuditStatus()) {
            case 1:
                vo.setAuditStatusText("待审核");
                break;
            case 2:
                vo.setAuditStatusText("已通过");
                break;
            case 3:
                vo.setAuditStatusText("已驳回");
                break;
            default:
                vo.setAuditStatusText("未知");
        }
        
        // 获取学生信息
        if (grade.getStudentId() != null) {
            StudentInfo student = studentInfoService.getById(grade.getStudentId());
            if (student != null) {
                vo.setStudentNo(student.getStudentNo());
                vo.setStudentName(student.getName());
            }
        }
        
        // 获取课程信息
        if (grade.getCourseScheduleId() != null) {
            CourseSchedule schedule = courseScheduleService.getById(grade.getCourseScheduleId());
            if (schedule != null && schedule.getCourseId() != null) {
                Course course = courseService.getById(schedule.getCourseId());
                if (course != null) {
                    vo.setCourseCode(course.getCourseCode());
                    vo.setCourseName(course.getCourseName());
                    vo.setCredits(course.getCredits());
                    vo.setCourseType(course.getCourseType());
                }
            }
        }
        
        // 获取教师信息
        if (grade.getTeacherId() != null) {
            SysUser teacher = sysUserService.getById(grade.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getRealName());
            }
        }
        
        // 获取审核员信息
        if (grade.getAuditorId() != null) {
            SysUser auditor = sysUserService.getById(grade.getAuditorId());
            if (auditor != null) {
                vo.setAuditorName(auditor.getRealName());
            }
        }
        
        return vo;
    }
}
