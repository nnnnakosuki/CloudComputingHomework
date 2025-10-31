package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程信息Mapper接口
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
