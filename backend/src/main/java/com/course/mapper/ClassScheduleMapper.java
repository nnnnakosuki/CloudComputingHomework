package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.ClassSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 排课Mapper接口
 */
@Mapper
public interface ClassScheduleMapper extends BaseMapper<ClassSchedule> {
}
