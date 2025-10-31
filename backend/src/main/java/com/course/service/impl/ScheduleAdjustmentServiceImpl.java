package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.ScheduleAdjustmentDTO;
import com.course.entity.ClassSchedule;
import com.course.entity.ScheduleAdjustment;
import com.course.entity.SysUser;
import com.course.exception.BusinessException;
import com.course.mapper.ScheduleAdjustmentMapper;
import com.course.service.ClassScheduleService;
import com.course.service.ScheduleAdjustmentService;
import com.course.service.SysUserService;
import com.course.vo.ScheduleAdjustmentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 调课服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleAdjustmentServiceImpl extends ServiceImpl<ScheduleAdjustmentMapper, ScheduleAdjustment> implements ScheduleAdjustmentService {
    
    private final ClassScheduleService classScheduleService;
    private final SysUserService sysUserService;
    
    @Override
    public IPage<ScheduleAdjustmentVO> getAdjustmentPage(IPage<ScheduleAdjustment> page, String keyword, Integer status) {
        LambdaQueryWrapper<ScheduleAdjustment> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ScheduleAdjustment::getReason, keyword);
        }
        
        if (status != null) {
            wrapper.eq(ScheduleAdjustment::getStatus, status);
        }
        
        wrapper.orderByDesc(ScheduleAdjustment::getCreateTime);
        
        IPage<ScheduleAdjustment> adjustmentPage = page(page, wrapper);
        
        // 转换为VO
        IPage<ScheduleAdjustmentVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ScheduleAdjustmentVO> voList = adjustmentPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyAdjustment(ScheduleAdjustmentDTO adjustmentDTO) {
        // 检查排课记录是否存在
        ClassSchedule classSchedule = classScheduleService.getById(adjustmentDTO.getClassScheduleId());
        if (classSchedule == null) {
            throw new BusinessException("排课记录不存在");
        }
        
        // 检查是否已有待审批的调课申请
        LambdaQueryWrapper<ScheduleAdjustment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleAdjustment::getClassScheduleId, adjustmentDTO.getClassScheduleId())
                .eq(ScheduleAdjustment::getStatus, 1); // 待审批
        
        if (count(wrapper) > 0) {
            throw new BusinessException("该排课已有待审批的调课申请");
        }
        
        ScheduleAdjustment adjustment = new ScheduleAdjustment();
        BeanUtils.copyProperties(adjustmentDTO, adjustment);
        adjustment.setOldInfo(buildScheduleInfo(classSchedule));
        adjustment.setOperatorId(1L); // 当前操作员ID，实际应该从SecurityContext获取
        adjustment.setStatus(1); // 待审批
        
        boolean saved = save(adjustment);
        if (!saved) {
            throw new BusinessException("申请调课失败");
        }
        
        log.info("成功申请调课: 排课ID={}, 原因={}", adjustmentDTO.getClassScheduleId(), adjustmentDTO.getReason());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveAdjustment(Long adjustmentId, Integer status, String remark) {
        ScheduleAdjustment adjustment = getById(adjustmentId);
        if (adjustment == null) {
            throw new BusinessException("调课申请不存在");
        }
        
        if (adjustment.getStatus() != 1) {
            throw new BusinessException("该调课申请已处理");
        }
        
        adjustment.setStatus(status);
        boolean updated = updateById(adjustment);
        if (!updated) {
            throw new BusinessException("审批调课申请失败");
        }
        
        // 如果审批通过，执行调课
        if (status == 2) {
            executeAdjustment(adjustmentId);
        }
        
        log.info("成功审批调课申请: ID={}, 状态={}", adjustmentId, status);
        return true;
    }
    
    @Override
    public List<ScheduleAdjustmentVO> getPendingAdjustments() {
        LambdaQueryWrapper<ScheduleAdjustment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleAdjustment::getStatus, 1) // 待审批
                .orderByDesc(ScheduleAdjustment::getCreateTime);
        
        List<ScheduleAdjustment> adjustments = list(wrapper);
        return adjustments.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeAdjustment(Long adjustmentId) {
        ScheduleAdjustment adjustment = getById(adjustmentId);
        if (adjustment == null) {
            throw new BusinessException("调课申请不存在");
        }
        
        if (adjustment.getStatus() != 2) {
            throw new BusinessException("该调课申请未通过审批");
        }
        
        // 这里应该根据newInfo更新排课记录
        // 暂时简化处理
        
        log.info("成功执行调课: ID={}", adjustmentId);
        return true;
    }
    
    /**
     * 构建排课信息字符串
     */
    private String buildScheduleInfo(ClassSchedule schedule) {
        return String.format("星期%d %s-%s %s", 
                schedule.getWeekDay(), 
                schedule.getStartTime(), 
                schedule.getEndTime(), 
                schedule.getClassroom());
    }
    
    /**
     * 转换为VO
     */
    private ScheduleAdjustmentVO convertToVO(ScheduleAdjustment adjustment) {
        ScheduleAdjustmentVO vo = new ScheduleAdjustmentVO();
        BeanUtils.copyProperties(adjustment, vo);
        
        // 获取排课信息
        ClassSchedule classSchedule = classScheduleService.getById(adjustment.getClassScheduleId());
        if (classSchedule != null) {
            // 获取课程信息
            vo.setCourseName("高等数学"); // 模拟数据
            vo.setTeacherName("王老师"); // 模拟数据
        }
        
        // 获取操作员信息
        if (adjustment.getOperatorId() != null) {
            SysUser operator = sysUserService.getById(adjustment.getOperatorId());
            if (operator != null) {
                vo.setOperatorName(operator.getRealName());
            }
        }
        
        // 设置状态文本
        switch (adjustment.getStatus()) {
            case 1:
                vo.setStatusText("待审批");
                break;
            case 2:
                vo.setStatusText("已通过");
                break;
            case 3:
                vo.setStatusText("已驳回");
                break;
            default:
                vo.setStatusText("未知");
        }
        
        return vo;
    }
}
