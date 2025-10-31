package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.CourseDTO;
import com.course.service.CourseService;
import com.course.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 课程管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    
    /**
     * 分页查询课程信息
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<CourseVO>> getCoursePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer courseType) {
        
        Page<com.course.entity.Course> page = new Page<>(current, size);
        IPage<CourseVO> result = courseService.getCoursePage(page, keyword, deptId, courseType);
        
        return Result.success(result);
    }
    
    /**
     * 根据ID获取课程信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<CourseVO> getCourseById(@PathVariable Long id) {
        com.course.entity.Course course = courseService.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        
        // 转换为VO
        CourseVO vo = new CourseVO();
        org.springframework.beans.BeanUtils.copyProperties(course, vo);
        
        return Result.success(vo);
    }
    
    /**
     * 添加课程
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addCourse(@Valid @RequestBody CourseDTO courseDTO) {
        try {
            boolean success = courseService.addCourse(courseDTO);
            if (success) {
                return Result.success("添加课程成功");
            } else {
                return Result.error("添加课程失败");
            }
        } catch (Exception e) {
            log.error("添加课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
        try {
            courseDTO.setId(id);
            boolean success = courseService.updateCourse(courseDTO);
            if (success) {
                return Result.success("更新课程信息成功");
            } else {
                return Result.error("更新课程信息失败");
            }
        } catch (Exception e) {
            log.error("更新课程信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteCourse(@PathVariable Long id) {
        try {
            boolean success = courseService.deleteCourse(id);
            if (success) {
                return Result.success("删除课程成功");
            } else {
                return Result.error("删除课程失败");
            }
        } catch (Exception e) {
            log.error("删除课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用课程
     */
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> changeCourseStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = courseService.changeCourseStatus(id, status);
            if (success) {
                return Result.success("更新课程状态成功");
            } else {
                return Result.error("更新课程状态失败");
            }
        } catch (Exception e) {
            log.error("更新课程状态失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有课程列表（用于下拉选择）
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<java.util.List<CourseVO>> getAllCourses() {
        try {
            java.util.List<com.course.entity.Course> courses = courseService.list();
            java.util.List<CourseVO> voList = courses.stream()
                    .map(course -> {
                        CourseVO vo = new CourseVO();
                        org.springframework.beans.BeanUtils.copyProperties(course, vo);
                        return vo;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            return Result.success(voList);
        } catch (Exception e) {
            log.error("获取课程列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
