package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.TeachingPlanDTO;
import com.course.entity.TeachingPlan;
import com.course.vo.TeachingPlanVO;

/**
 * 教学计划服务接口
 */
public interface TeachingPlanService extends IService<TeachingPlan> {
    
    /**
     * 分页查询教学计划
     */
    IPage<TeachingPlanVO> getPlanPage(IPage<TeachingPlan> page, String keyword, Long deptId, String major, String grade);
    
    /**
     * 添加教学计划
     */
    boolean addPlan(TeachingPlanDTO planDTO);
    
    /**
     * 更新教学计划
     */
    boolean updatePlan(TeachingPlanDTO planDTO);
    
    /**
     * 删除教学计划
     */
    boolean deletePlan(Long id);
    
    /**
     * 启用/禁用教学计划
     */
    boolean changePlanStatus(Long id, Integer status);
}
