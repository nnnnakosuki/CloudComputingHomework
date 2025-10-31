package com.course.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.TeachingPlanDTO;
import com.course.service.TeachingPlanService;
import com.course.vo.TeachingPlanVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 教学计划管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/teaching-plans")
@RequiredArgsConstructor
public class TeachingPlanController {
    
    private final TeachingPlanService teachingPlanService;
    
    /**
     * 分页查询教学计划
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<TeachingPlanVO>> getPlanPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String grade) {
        
        Page<com.course.entity.TeachingPlan> page = new Page<>(current, size);
        IPage<TeachingPlanVO> result = teachingPlanService.getPlanPage(page, keyword, deptId, major, grade);
        
        return Result.success(result);
    }
    
    /**
     * 根据ID获取教学计划
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<TeachingPlanVO> getPlanById(@PathVariable Long id) {
        com.course.entity.TeachingPlan plan = teachingPlanService.getById(id);
        if (plan == null) {
            return Result.error("教学计划不存在");
        }
        
        // 转换为VO
        TeachingPlanVO vo = new TeachingPlanVO();
        org.springframework.beans.BeanUtils.copyProperties(plan, vo);
        
        return Result.success(vo);
    }
    
    /**
     * 添加教学计划
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addPlan(@Valid @RequestBody TeachingPlanDTO planDTO) {
        try {
            boolean success = teachingPlanService.addPlan(planDTO);
            if (success) {
                return Result.success("添加教学计划成功");
            } else {
                return Result.error("添加教学计划失败");
            }
        } catch (Exception e) {
            log.error("添加教学计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新教学计划
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updatePlan(@PathVariable Long id, @Valid @RequestBody TeachingPlanDTO planDTO) {
        try {
            planDTO.setId(id);
            boolean success = teachingPlanService.updatePlan(planDTO);
            if (success) {
                return Result.success("更新教学计划成功");
            } else {
                return Result.error("更新教学计划失败");
            }
        } catch (Exception e) {
            log.error("更新教学计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除教学计划
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deletePlan(@PathVariable Long id) {
        try {
            boolean success = teachingPlanService.deletePlan(id);
            if (success) {
                return Result.success("删除教学计划成功");
            } else {
                return Result.error("删除教学计划失败");
            }
        } catch (Exception e) {
            log.error("删除教学计划失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用教学计划
     */
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> changePlanStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = teachingPlanService.changePlanStatus(id, status);
            if (success) {
                return Result.success("更新教学计划状态成功");
            } else {
                return Result.error("更新教学计划状态失败");
            }
        } catch (Exception e) {
            log.error("更新教学计划状态失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
