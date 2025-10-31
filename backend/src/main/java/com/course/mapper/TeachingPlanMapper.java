package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.TeachingPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教学计划Mapper接口
 */
@Mapper
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {
}
