package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.StudentStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学籍变动Mapper接口
 */
@Mapper
public interface StudentStatusMapper extends BaseMapper<StudentStatus> {
}
