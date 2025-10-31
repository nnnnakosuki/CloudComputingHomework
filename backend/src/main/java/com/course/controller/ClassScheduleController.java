package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.ClassScheduleDTO;
import com.course.service.ClassScheduleService;
import com.course.vo.ClassScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 排课管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/class-schedules")
@RequiredArgsConstructor
public class ClassScheduleController {
    
    private final ClassScheduleService classScheduleService;
    
    /**
     * 分页查询排课信息
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<ClassScheduleVO>> getSchedulePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) Integer weekDay) {
        
        Page<com.course.entity.ClassSchedule> page = new Page<>(current, size);
        IPage<ClassScheduleVO> result = classScheduleService.getSchedulePage(page, keyword, semester, teacherId, weekDay);
        
        return Result.success(result);
    }
    
    /**
     * 添加排课
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addSchedule(@Valid @RequestBody ClassScheduleDTO scheduleDTO) {
        try {
            boolean success = classScheduleService.addSchedule(scheduleDTO);
            if (success) {
                return Result.success();
            } else {
                return Result.error("添加排课失败");
            }
        } catch (Exception e) {
            log.error("添加排课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新排课
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateSchedule(@PathVariable Long id, @Valid @RequestBody ClassScheduleDTO scheduleDTO) {
        try {
            scheduleDTO.setId(id);
            boolean success = classScheduleService.updateSchedule(scheduleDTO);
            if (success) {
                return Result.success("更新排课成功");
            } else {
                return Result.error("更新排课失败");
            }
        } catch (Exception e) {
            log.error("更新排课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除排课
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteSchedule(@PathVariable Long id) {
        try {
            boolean success = classScheduleService.deleteSchedule(id);
            if (success) {
                return Result.success("删除排课成功");
            } else {
                return Result.error("删除排课失败");
            }
        } catch (Exception e) {
            log.error("删除排课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量排课
     */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchAddSchedules(@Valid @RequestBody List<ClassScheduleDTO> scheduleList) {
        try {
            boolean success = classScheduleService.batchAddSchedules(scheduleList);
            if (success) {
                return Result.success("批量排课成功");
            } else {
                return Result.error("批量排课失败");
            }
        } catch (Exception e) {
            log.error("批量排课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查时间冲突
     */
    @PostMapping("/check-conflict")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> checkTimeConflict(@RequestBody ClassScheduleDTO scheduleDTO) {
        try {
            boolean hasConflict = classScheduleService.checkTimeConflict(
                    scheduleDTO.getCourseScheduleId(),
                    scheduleDTO.getWeekDay(),
                    scheduleDTO.getStartTime(),
                    scheduleDTO.getEndTime(),
                    scheduleDTO.getClassroom()
            );
            return Result.success(hasConflict);
        } catch (Exception e) {
            log.error("检查时间冲突失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取周课表
     */
    @GetMapping("/weekly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public Result<List<ClassScheduleVO>> getWeeklySchedule(
            @RequestParam String semester,
            @RequestParam(required = false) Integer weekDay) {
        try {
            List<ClassScheduleVO> schedules = classScheduleService.getWeeklySchedule(semester, weekDay);
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取周课表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取教师课表
     */
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<ClassScheduleVO>> getTeacherSchedule(
            @PathVariable Long teacherId,
            @RequestParam String semester) {
        try {
            List<ClassScheduleVO> schedules = classScheduleService.getTeacherSchedule(teacherId, semester);
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取教师课表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取学生课表
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public Result<List<ClassScheduleVO>> getStudentSchedule(
            @PathVariable Long studentId,
            @RequestParam String semester) {
        try {
            List<ClassScheduleVO> schedules = classScheduleService.getStudentSchedule(studentId, semester);
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取学生课表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
