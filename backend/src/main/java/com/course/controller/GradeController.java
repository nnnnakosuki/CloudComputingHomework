package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.GradeInputDTO;
import com.course.entity.StudentInfo;
import com.course.entity.SysUser;
import com.course.service.CourseGradeService;
import com.course.service.StudentInfoService;
import com.course.service.SysUserService;
import com.course.vo.CourseGradeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 成绩管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {

    private final CourseGradeService courseGradeService;
    private final StudentInfoService studentInfoService;
    private final SysUserService sysUserService;

    /**
     * 分页查询成绩信息
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<CourseGradeVO>> getGradePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseScheduleId,
            @RequestParam(required = false) Integer auditStatus) {

        Page<com.course.entity.CourseGrade> page = new Page<>(current, size);
        IPage<CourseGradeVO> result = courseGradeService.getGradePage(page, studentId, courseScheduleId, auditStatus);

        return Result.success(result);
    }

    /**
     * 教师录入成绩
     */
    @PostMapping("/input")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> inputGrade(@Valid @RequestBody GradeInputDTO gradeInputDTO) {
        try {
            boolean success = courseGradeService.inputGrade(gradeInputDTO);
            if (success) {
                return Result.success("录入成绩成功");
            } else {
                return Result.error("录入成绩失败");
            }
        } catch (Exception e) {
            log.error("录入成绩失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量录入成绩
     */
    @PostMapping("/batch-input")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> batchInputGrades(@RequestParam Long courseScheduleId, @Valid @RequestBody List<GradeInputDTO> gradeList) {
        try {
            boolean success = courseGradeService.batchInputGrades(courseScheduleId, gradeList);
            if (success) {
                return Result.success("批量录入成绩成功");
            } else {
                return Result.error("批量录入成绩失败");
            }
        } catch (Exception e) {
            log.error("批量录入成绩失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核成绩
     */
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> auditGrade(@PathVariable Long id, @RequestParam Integer auditStatus, @RequestParam(required = false) String remark) {
        try {
            boolean success = courseGradeService.auditGrade(id, auditStatus, remark);
            if (success) {
                return Result.success("审核成绩成功");
            } else {
                return Result.error("审核成绩失败");
            }
        } catch (Exception e) {
            log.error("审核成绩失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据学生ID获取成绩列表（带时段校验）
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<List<CourseGradeVO>> getGradesByStudent(@PathVariable Long studentId) {
        try {
            // 如果是学生角色，需要从认证上下文获取真实的学生ID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // 获取用户信息
            SysUser user = sysUserService.getByUsername(username);
            if (user != null) {
                // 如果角色是学生，验证并获取学生信息
                StudentInfo student = studentInfoService.getByUserId(user.getId());
                if (student != null) {
                    // 使用真实的学生ID
                    studentId = student.getId();
                }
            }

            Page<com.course.entity.CourseGrade> page = new Page<>(1, 100);
            IPage<CourseGradeVO> result = courseGradeService.getGradePage(page, studentId, null, null);

            // 如果是学生查询，需要检查时段
            List<CourseGradeVO> grades = result.getRecords();
            for (CourseGradeVO grade : grades) {
                if (grade.getQueryStartTime() != null && grade.getQueryEndTime() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(grade.getQueryStartTime()) || now.isAfter(grade.getQueryEndTime())) {
                        // 不在查询时段，清空敏感信息
                        grade.setScore(null);
                        grade.setAuditStatusText("查询未开放");
                    }
                }
            }

            return Result.success(grades);
        } catch (Exception e) {
            log.error("获取学生成绩失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * Excel/CSV导入成绩
     */
    @PostMapping("/import")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> importGrades(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            log.info("文件上传信息: 文件名={}, 内容类型={}, 文件大小={} bytes", fileName, contentType, fileSize);

            List<com.course.dto.GradeImportDTO> importList;

            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                log.info("尝试解析Excel文件...");
                importList = com.course.utils.GradeFileParser.parseExcel(file.getInputStream());
                log.info("Excel解析完成，共{}条记录", importList.size());
            } else if (fileName.endsWith(".csv")) {
                log.info("尝试解析CSV文件...");
                importList = com.course.utils.GradeFileParser.parseCSV(file.getInputStream());
                log.info("CSV解析完成，共{}条记录", importList.size());
            } else {
                log.error("不支持的文件格式: {}", fileName);
                return Result.error("不支持的文件格式，请上传Excel或CSV文件");
            }

            boolean success = courseGradeService.importGrades(importList);
            if (success) {
                return Result.success("成绩导入成功，共导入" + importList.size() + "条记录");
            } else {
                return Result.error("成绩导入失败");
            }
        } catch (Exception e) {
            log.error("导入成绩失败: {}", e.getMessage(), e);
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    /**
     * 设置成绩查询时段
     */
    @PostMapping("/query-time")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> setQueryTime(
            @RequestParam Long courseScheduleId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            boolean success = courseGradeService.setQueryTime(courseScheduleId, start, end);
            if (success) {
                return Result.success("设置查询时段成功");
            } else {
                return Result.error("设置查询时段失败");
            }
        } catch (Exception e) {
            log.error("设置查询时段失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除成绩
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public Result<String> deleteGrade(@PathVariable Long id) {
        try {
            boolean success = courseGradeService.removeById(id);
            if (success) {
                return Result.success("删除成绩成功");
            } else {
                return Result.error("删除成绩失败");
            }
        } catch (Exception e) {
            log.error("删除成绩失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
