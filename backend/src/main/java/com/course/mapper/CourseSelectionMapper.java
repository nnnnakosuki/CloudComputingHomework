package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.CourseSelection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 选课记录Mapper接口
 */
@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {
}
