package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.CourseSelectionDTO;
import com.course.entity.StudentInfo;
import com.course.service.CourseSelectionService;
import com.course.service.StudentInfoService;
import com.course.service.SysUserService;
import com.course.vo.CourseSelectionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 选课管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/course-selections")
@RequiredArgsConstructor
public class CourseSelectionController {
    
    private final CourseSelectionService courseSelectionService;
    private final StudentInfoService studentInfoService;
    private final SysUserService sysUserService;
    
    /**
     * 分页查询选课记录
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<CourseSelectionVO>> getSelectionPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseScheduleId,
            @RequestParam(required = false) Integer status) {
        
        Page<com.course.entity.CourseSelection> page = new Page<>(current, size);
        IPage<CourseSelectionVO> result = courseSelectionService.getSelectionPage(page, studentId, courseScheduleId, status);
        
        return Result.success(result);
    }
    
    /**
     * 学生选课
     */
    @PostMapping("/select")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> selectCourse(@Valid @RequestBody CourseSelectionDTO selectionDTO) {
        try {
            // 获取当前登录用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // 根据用户名获取用户信息
            com.course.entity.SysUser user = sysUserService.getByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 根据用户ID获取学生信息
            StudentInfo student = studentInfoService.getByUserId(user.getId());
            if (student == null) {
                return Result.error("学生信息不存在");
            }
            
            // 设置学生ID
            selectionDTO.setStudentId(student.getId());
            
            boolean success = courseSelectionService.selectCourse(selectionDTO);
            if (success) {
                return Result.success("选课成功");
            } else {
                return Result.error("选课失败");
            }
        } catch (Exception e) {
            log.error("选课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 学生退课
     */
    @PostMapping("/{id}/drop")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> dropCourse(@PathVariable Long id) {
        try {
            boolean success = courseSelectionService.dropCourse(id);
            if (success) {
                return Result.success("退课成功");
            } else {
                return Result.error("退课失败");
            }
        } catch (Exception e) {
            log.error("退课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量确认选课
     */
    @PostMapping("/batch-confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchConfirmSelections(@RequestBody List<Long> selectionIds) {
        try {
            boolean success = courseSelectionService.batchConfirmSelections(selectionIds);
            if (success) {
                return Result.success("批量确认选课成功");
            } else {
                return Result.error("批量确认选课失败");
            }
        } catch (Exception e) {
            log.error("批量确认选课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量拒绝选课
     */
    @PostMapping("/batch-reject")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> batchRejectSelections(@RequestBody List<Long> selectionIds) {
        try {
            boolean success = courseSelectionService.batchRejectSelections(selectionIds);
            if (success) {
                return Result.success("批量拒绝选课成功");
            } else {
                return Result.error("批量拒绝选课失败");
            }
        } catch (Exception e) {
            log.error("批量拒绝选课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取学生选课列表
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<CourseSelectionVO>> getStudentSelections(
            @PathVariable Long studentId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseScheduleId) {
        try {
            // 参数校验
            if (studentId == null) {
                return Result.error("学生ID不能为空");
            }
            
            List<CourseSelectionVO> selections = courseSelectionService.getStudentSelections(studentId, semester);
            return Result.success(selections);
        } catch (Exception e) {
            log.error("获取学生选课列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前学生选课列表
     */
    @GetMapping("/my-selections")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<CourseSelectionVO>> getMySelections(
            @RequestParam(required = false) String semester) {
        try {
            // 获取当前登录用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // 根据用户名获取用户信息
            com.course.entity.SysUser user = sysUserService.getByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 根据用户ID获取学生信息
            StudentInfo student = studentInfoService.getByUserId(user.getId());
            if (student == null) {
                return Result.error("学生信息不存在");
            }
            
            List<CourseSelectionVO> selections = courseSelectionService.getStudentSelections(student.getId(), semester);
            return Result.success(selections);
        } catch (Exception e) {
            log.error("获取学生选课列表失败: {}", e.getMessage());
            return Result.error("获取学生选课列表失败");
        }
    }
}
