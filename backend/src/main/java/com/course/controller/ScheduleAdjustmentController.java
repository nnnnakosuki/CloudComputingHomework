package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.ScheduleAdjustmentDTO;
import com.course.service.ScheduleAdjustmentService;
import com.course.vo.ScheduleAdjustmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 调课管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/schedule-adjustments")
@RequiredArgsConstructor
public class ScheduleAdjustmentController {
    
    private final ScheduleAdjustmentService scheduleAdjustmentService;
    
    /**
     * 分页查询调课记录
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<IPage<ScheduleAdjustmentVO>> getAdjustmentPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        Page<com.course.entity.ScheduleAdjustment> page = new Page<>(current, size);
        IPage<ScheduleAdjustmentVO> result = scheduleAdjustmentService.getAdjustmentPage(page, keyword, status);
        
        return Result.success(result);
    }
    
    /**
     * 申请调课
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<String> applyAdjustment(@Valid @RequestBody ScheduleAdjustmentDTO adjustmentDTO) {
        try {
            boolean success = scheduleAdjustmentService.applyAdjustment(adjustmentDTO);
            if (success) {
                return Result.success("申请调课成功");
            } else {
                return Result.error("申请调课失败");
            }
        } catch (Exception e) {
            log.error("申请调课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审批调课
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> approveAdjustment(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        try {
            boolean success = scheduleAdjustmentService.approveAdjustment(id, status, remark);
            if (success) {
                return Result.success("审批调课成功");
            } else {
                return Result.error("审批调课失败");
            }
        } catch (Exception e) {
            log.error("审批调课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取待审批的调课申请
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<ScheduleAdjustmentVO>> getPendingAdjustments() {
        try {
            List<ScheduleAdjustmentVO> adjustments = scheduleAdjustmentService.getPendingAdjustments();
            return Result.success(adjustments);
        } catch (Exception e) {
            log.error("获取待审批调课申请失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 执行调课
     */
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> executeAdjustment(@PathVariable Long id) {
        try {
            boolean success = scheduleAdjustmentService.executeAdjustment(id);
            if (success) {
                return Result.success("执行调课成功");
            } else {
                return Result.error("执行调课失败");
            }
        } catch (Exception e) {
            log.error("执行调课失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
