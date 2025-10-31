package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.CourseGrade;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface CourseGradeMapper extends BaseMapper<CourseGrade> {
}
