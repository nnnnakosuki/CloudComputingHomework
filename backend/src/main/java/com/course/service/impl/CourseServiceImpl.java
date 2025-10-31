package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.CourseDTO;
import com.course.entity.Course;
import com.course.exception.BusinessException;
import com.course.mapper.CourseMapper;
import com.course.service.CourseService;
import com.course.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    
    @Override
    public IPage<CourseVO> getCoursePage(IPage<Course> page, String keyword, Long deptId, Integer courseType) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Course::getCourseCode, keyword)
                    .or().like(Course::getCourseName, keyword));
        }
        
        if (deptId != null) {
            wrapper.eq(Course::getDeptId, deptId);
        }
        
        if (courseType != null) {
            wrapper.eq(Course::getCourseType, courseType);
        }
        
        wrapper.orderByDesc(Course::getCreateTime);
        
        IPage<Course> coursePage = page(page, wrapper);
        
        // 转换为VO
        IPage<CourseVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<CourseVO> voList = coursePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCourse(CourseDTO courseDTO) {
        // 检查课程编号是否已存在
        Course existCourse = getByCourseCode(courseDTO.getCourseCode());
        if (existCourse != null) {
            throw new BusinessException("课程编号已存在");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        course.setStatus(1); // 启用状态
        
        boolean saved = save(course);
        if (!saved) {
            throw new BusinessException("添加课程失败");
        }
        
        log.info("成功添加课程: {}", courseDTO.getCourseName());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourse(CourseDTO courseDTO) {
        Course existCourse = getById(courseDTO.getId());
        if (existCourse == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查课程编号是否被其他课程使用
        Course courseByCode = getByCourseCode(courseDTO.getCourseCode());
        if (courseByCode != null && !courseByCode.getId().equals(courseDTO.getId())) {
            throw new BusinessException("课程编号已被其他课程使用");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseDTO, course);
        
        boolean updated = updateById(course);
        if (!updated) {
            throw new BusinessException("更新课程信息失败");
        }
        
        log.info("成功更新课程信息: {}", courseDTO.getCourseName());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCourse(Long id) {
        Course course = getById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查是否有关联的开课计划
        // 这里应该检查course_schedule表，暂时跳过
        
        boolean deleted = removeById(id);
        if (!deleted) {
            throw new BusinessException("删除课程失败");
        }
        
        log.info("成功删除课程: {}", course.getCourseName());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeCourseStatus(Long id, Integer status) {
        Course course = getById(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        course.setStatus(status);
        boolean updated = updateById(course);
        if (!updated) {
            throw new BusinessException("更新课程状态失败");
        }
        
        log.info("成功更新课程状态: ID={}, 状态={}", id, status);
        return true;
    }
    
    @Override
    public Course getByCourseCode(String courseCode) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourseCode, courseCode);
        return getOne(wrapper);
    }
    
    /**
     * 转换为VO
     */
    private CourseVO convertToVO(Course course) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(course, vo);
        
        // 设置课程类型文本
        vo.setCourseTypeText(course.getCourseType() == 1 ? "必修" : "选修");
        
        // 设置状态文本
        vo.setStatusText(course.getStatus() == 1 ? "启用" : "禁用");
        
        // 设置院系名称
        if (course.getDeptId() != null) {
            switch (course.getDeptId().intValue()) {
                case 1:
                    vo.setDeptName("计算机学院");
                    break;
                case 2:
                    vo.setDeptName("软件学院");
                    break;
                default:
                    vo.setDeptName("未知院系");
            }
        }
        
        return vo;
    }
}
