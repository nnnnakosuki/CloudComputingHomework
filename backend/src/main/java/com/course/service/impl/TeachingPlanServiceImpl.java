package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.TeachingPlanDTO;
import com.course.entity.TeachingPlan;
import com.course.exception.BusinessException;
import com.course.mapper.TeachingPlanMapper;
import com.course.service.TeachingPlanService;
import com.course.vo.TeachingPlanVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 教学计划服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeachingPlanServiceImpl extends ServiceImpl<TeachingPlanMapper, TeachingPlan> implements TeachingPlanService {
    
    @Override
    public IPage<TeachingPlanVO> getPlanPage(IPage<TeachingPlan> page, String keyword, Long deptId, String major, String grade) {
        LambdaQueryWrapper<TeachingPlan> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(TeachingPlan::getPlanName, keyword)
                    .or().like(TeachingPlan::getMajor, keyword)
                    .or().like(TeachingPlan::getGrade, keyword));
        }
        
        if (deptId != null) {
            wrapper.eq(TeachingPlan::getDeptId, deptId);
        }
        
        if (StringUtils.hasText(major)) {
            wrapper.like(TeachingPlan::getMajor, major);
        }
        
        if (StringUtils.hasText(grade)) {
            wrapper.eq(TeachingPlan::getGrade, grade);
        }
        
        wrapper.orderByDesc(TeachingPlan::getCreateTime);
        
        IPage<TeachingPlan> planPage = page(page, wrapper);
        
        // 转换为VO
        IPage<TeachingPlanVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<TeachingPlanVO> voList = planPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPlan(TeachingPlanDTO planDTO) {
        // 检查计划名称是否已存在
        LambdaQueryWrapper<TeachingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingPlan::getPlanName, planDTO.getPlanName())
                .eq(TeachingPlan::getMajor, planDTO.getMajor())
                .eq(TeachingPlan::getGrade, planDTO.getGrade());
        
        if (count(wrapper) > 0) {
            throw new BusinessException("该专业年级的教学计划已存在");
        }
        
        TeachingPlan plan = new TeachingPlan();
        BeanUtils.copyProperties(planDTO, plan);
        plan.setStatus(1); // 启用状态
        
        boolean saved = save(plan);
        if (!saved) {
            throw new BusinessException("添加教学计划失败");
        }
        
        log.info("成功添加教学计划: {}", planDTO.getPlanName());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePlan(TeachingPlanDTO planDTO) {
        TeachingPlan existPlan = getById(planDTO.getId());
        if (existPlan == null) {
            throw new BusinessException("教学计划不存在");
        }
        
        // 检查计划名称是否被其他计划使用
        LambdaQueryWrapper<TeachingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachingPlan::getPlanName, planDTO.getPlanName())
                .eq(TeachingPlan::getMajor, planDTO.getMajor())
                .eq(TeachingPlan::getGrade, planDTO.getGrade())
                .ne(TeachingPlan::getId, planDTO.getId());
        
        if (count(wrapper) > 0) {
            throw new BusinessException("该专业年级的教学计划已存在");
        }
        
        TeachingPlan plan = new TeachingPlan();
        BeanUtils.copyProperties(planDTO, plan);
        
        boolean updated = updateById(plan);
        if (!updated) {
            throw new BusinessException("更新教学计划失败");
        }
        
        log.info("成功更新教学计划: {}", planDTO.getPlanName());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlan(Long id) {
        TeachingPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException("教学计划不存在");
        }
        
        // 检查是否有关联的开课计划
        // 这里应该检查course_schedule表，暂时跳过
        
        boolean deleted = removeById(id);
        if (!deleted) {
            throw new BusinessException("删除教学计划失败");
        }
        
        log.info("成功删除教学计划: {}", plan.getPlanName());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePlanStatus(Long id, Integer status) {
        TeachingPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException("教学计划不存在");
        }
        
        plan.setStatus(status);
        boolean updated = updateById(plan);
        if (!updated) {
            throw new BusinessException("更新教学计划状态失败");
        }
        
        log.info("成功更新教学计划状态: ID={}, 状态={}", id, status);
        return true;
    }
    
    /**
     * 转换为VO
     */
    private TeachingPlanVO convertToVO(TeachingPlan plan) {
        TeachingPlanVO vo = new TeachingPlanVO();
        BeanUtils.copyProperties(plan, vo);
        
        // 设置状态文本
        vo.setStatusText(plan.getStatus() == 1 ? "启用" : "禁用");
        
        // 设置院系名称
        switch (plan.getDeptId().intValue()) {
            case 1:
                vo.setDeptName("计算机学院");
                break;
            case 2:
                vo.setDeptName("软件学院");
                break;
            default:
                vo.setDeptName("未知院系");
        }
        
        return vo;
    }
}
