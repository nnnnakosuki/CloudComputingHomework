package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开课计划Mapper接口
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
}
