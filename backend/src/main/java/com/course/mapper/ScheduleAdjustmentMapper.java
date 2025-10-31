package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.ScheduleAdjustment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 调课记录Mapper接口
 */
@Mapper
public interface ScheduleAdjustmentMapper extends BaseMapper<ScheduleAdjustment> {
}
