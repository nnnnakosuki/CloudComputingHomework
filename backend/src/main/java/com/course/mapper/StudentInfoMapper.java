package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生信息Mapper接口
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
}
