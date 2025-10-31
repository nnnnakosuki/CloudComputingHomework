package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.ScheduleAdjustmentDTO;
import com.course.entity.ScheduleAdjustment;
import com.course.vo.ScheduleAdjustmentVO;

import java.util.List;

/**
 * 调课服务接口
 */
public interface ScheduleAdjustmentService extends IService<ScheduleAdjustment> {
    
    /**
     * 分页查询调课记录
     */
    IPage<ScheduleAdjustmentVO> getAdjustmentPage(IPage<ScheduleAdjustment> page, String keyword, Integer status);
    
    /**
     * 申请调课
     */
    boolean applyAdjustment(ScheduleAdjustmentDTO adjustmentDTO);
    
    /**
     * 审批调课
     */
    boolean approveAdjustment(Long adjustmentId, Integer status, String remark);
    
    /**
     * 获取待审批的调课申请
     */
    List<ScheduleAdjustmentVO> getPendingAdjustments();
    
    /**
     * 执行调课
     */
    boolean executeAdjustment(Long adjustmentId);
}
