package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典Mapper接口
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {
}
