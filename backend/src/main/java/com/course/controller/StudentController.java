package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.StudentInfoDTO;
import com.course.dto.StudentStatusChangeDTO;
import com.course.service.StudentInfoService;
import com.course.vo.StudentInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 学生管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentInfoService studentInfoService;
    
    /**
     * 分页查询学生信息
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMIN')")
    public Result<IPage<StudentInfoVO>> getStudentPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        
        Page<com.course.entity.StudentInfo> page = new Page<>(current, size);
        IPage<StudentInfoVO> result = studentInfoService.getStudentPage(page, keyword, deptId, status);
        
        return Result.success(result);
    }
    
    /**
     * 根据ID获取学生信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMIN')")
    public Result<StudentInfoVO> getStudentById(@PathVariable Long id) {
        com.course.entity.StudentInfo student = studentInfoService.getById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        
        // 转换为VO
        StudentInfoVO vo = new StudentInfoVO();
        org.springframework.beans.BeanUtils.copyProperties(student, vo);
        
        return Result.success(vo);
    }
    
    /**
     * 添加学生
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addStudent(@Valid @RequestBody StudentInfoDTO studentInfoDTO) {
        try {
            boolean success = studentInfoService.addStudent(studentInfoDTO);
            if (success) {
                return Result.success("添加学生成功");
            } else {
                return Result.error("添加学生失败");
            }
        } catch (Exception e) {
            log.error("添加学生失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新学生信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentInfoDTO studentInfoDTO) {
        try {
            studentInfoDTO.setId(id);
            boolean success = studentInfoService.updateStudent(studentInfoDTO);
            if (success) {
                return Result.success("更新学生信息成功");
            } else {
                return Result.error("更新学生信息失败");
            }
        } catch (Exception e) {
            log.error("更新学生信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteStudent(@PathVariable Long id) {
        try {
            boolean success = studentInfoService.deleteStudent(id);
            if (success) {
                return Result.success("删除学生成功");
            } else {
                return Result.error("删除学生失败");
            }
        } catch (Exception e) {
            log.error("删除学生失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 学籍变动处理
     */
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> changeStudentStatus(@PathVariable Long id, @Valid @RequestBody StudentStatusChangeDTO statusChangeDTO) {
        try {
            statusChangeDTO.setStudentId(id);
            boolean success = studentInfoService.changeStudentStatus(statusChangeDTO);
            if (success) {
                return Result.success("学籍变动处理成功");
            } else {
                return Result.error("学籍变动处理失败");
            }
        } catch (Exception e) {
            log.error("学籍变动处理失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
