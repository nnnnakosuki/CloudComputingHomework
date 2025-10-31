package com.course.controller;

import com.course.common.Result;
import com.course.entity.StudentInfo;
import com.course.service.StudentInfoService;
import com.course.service.SysUserService;
import com.course.vo.StudentInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 学生个人控制器
 */
@Slf4j
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentProfileController {
    
    private final StudentInfoService studentInfoService;
    private final SysUserService sysUserService;
    
    /**
     * 获取当前学生个人信息
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<StudentInfoVO> getCurrentStudentProfile() {
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
            
            // 转换为VO
            StudentInfoVO vo = convertToVO(student);
            
            return Result.success(vo);
        } catch (Exception e) {
            log.error("获取学生个人信息失败", e);
            return Result.error("获取学生个人信息失败");
        }
    }
    
    /**
     * 更新当前学生个人信息
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<String> updateCurrentStudentProfile(@RequestBody StudentInfoVO studentVO) {
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
            
            // 只允许学生更新部分字段
            student.setName(studentVO.getName());
            student.setGender(studentVO.getGender());
            student.setMajor(studentVO.getMajor());
            student.setClassName(studentVO.getClassName());
            
            boolean success = studentInfoService.updateById(student);
            if (success) {
                return Result.success("更新个人信息成功");
            } else {
                return Result.error("更新个人信息失败");
            }
        } catch (Exception e) {
            log.error("更新学生个人信息失败", e);
            return Result.error("更新学生个人信息失败");
        }
    }
    
    /**
     * 转换为VO对象
     */
    private StudentInfoVO convertToVO(StudentInfo student) {
        StudentInfoVO vo = new StudentInfoVO();
        BeanUtils.copyProperties(student, vo);
        
        // 设置性别文本
        vo.setGenderText(student.getGender() == 1 ? "男" : "女");
        
        // 设置状态文本
        switch (student.getStatus()) {
            case 1:
                vo.setStatusText("在校");
                break;
            case 2:
                vo.setStatusText("休学");
                break;
            case 3:
                vo.setStatusText("退学");
                break;
            case 4:
                vo.setStatusText("毕业");
                break;
            default:
                vo.setStatusText("未知");
        }
        
        // 获取用户信息
        if (student.getUserId() != null) {
            com.course.entity.SysUser user = sysUserService.getById(student.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
            }
        }
        
        return vo;
    }
}
