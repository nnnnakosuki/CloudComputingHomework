package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.entity.SysDict;

import java.util.List;

/**
 * 数据字典服务接口
 */
public interface SysDictService extends IService<SysDict> {
    
    /**
     * 分页查询数据字典
     */
    IPage<SysDict> getDictPage(IPage<SysDict> page, String keyword, String dictType);
    
    /**
     * 根据字典类型获取字典列表
     */
    List<SysDict> getDictByType(String dictType);
    
    /**
     * 获取所有字典类型
     */
    List<String> getAllDictTypes();
}
