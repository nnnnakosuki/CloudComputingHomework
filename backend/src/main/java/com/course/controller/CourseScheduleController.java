package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.CourseScheduleDTO;
import com.course.service.CourseScheduleService;
import com.course.vo.CourseScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 开课计划管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/course-schedules")
@RequiredArgsConstructor
public class CourseScheduleController {
    
    private final CourseScheduleService courseScheduleService;
    
    /**
     * 分页查询开课计划
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<CourseScheduleVO>> getSchedulePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer status) {
        
        Page<com.course.entity.CourseSchedule> page = new Page<>(current, size);
        IPage<CourseScheduleVO> result = courseScheduleService.getSchedulePage(page, keyword, semester, teacherId, status);
        
        return Result.success(result);
    }
    
    /**
     * 发布开课计划
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> publishSchedule(@Valid @RequestBody CourseScheduleDTO scheduleDTO) {
        try {
            boolean success = courseScheduleService.publishSchedule(scheduleDTO);
            if (success) {
                return Result.success("发布开课计划成功");
            } else {
                return Result.error("发布开课计划失败");
            }
        } catch (Exception e) {
            log.error("发布开课计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新开课计划
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateSchedule(@PathVariable Long id, @Valid @RequestBody CourseScheduleDTO scheduleDTO) {
        try {
            scheduleDTO.setId(id);
            boolean success = courseScheduleService.updateSchedule(scheduleDTO);
            if (success) {
                return Result.success("更新开课计划成功");
            } else {
                return Result.error("更新开课计划失败");
            }
        } catch (Exception e) {
            log.error("更新开课计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除开课计划
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteSchedule(@PathVariable Long id) {
        try {
            boolean success = courseScheduleService.deleteSchedule(id);
            if (success) {
                return Result.success("删除开课计划成功");
            } else {
                return Result.error("删除开课计划失败");
            }
        } catch (Exception e) {
            log.error("删除开课计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用开课计划
     */
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> changeScheduleStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = courseScheduleService.changeScheduleStatus(id, status);
            if (success) {
                return Result.success("更新开课计划状态成功");
            } else {
                return Result.error("更新开课计划状态失败");
            }
        } catch (Exception e) {
            log.error("更新开课计划状态失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取可选课程列表（学生端）
     */
    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<CourseScheduleVO>> getAvailableCourses(
            @RequestParam Long studentId,
            @RequestParam String semester) {
        try {
            List<CourseScheduleVO> courses = courseScheduleService.getAvailableCourses(studentId, semester);
            return Result.success(courses);
        } catch (Exception e) {
            log.error("获取可选课程列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
