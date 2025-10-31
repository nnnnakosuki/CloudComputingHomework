package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.entity.SysDict;
import com.course.mapper.SysDictMapper;
import com.course.service.SysDictService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典服务实现类
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {
    
    @Override
    public IPage<SysDict> getDictPage(IPage<SysDict> page, String keyword, String dictType) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysDict::getDictType, keyword)
                    .or().like(SysDict::getDictLabel, keyword)
                    .or().like(SysDict::getDictValue, keyword));
        }
        
        // 字典类型筛选
        if (StringUtils.hasText(dictType)) {
            wrapper.eq(SysDict::getDictType, dictType);
        }
        
        wrapper.orderByAsc(SysDict::getDictType, SysDict::getSortOrder);
        
        return page(page, wrapper);
    }
    
    @Override
    public List<SysDict> getDictByType(String dictType) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictType, dictType)
                .eq(SysDict::getStatus, 1)
                .orderByAsc(SysDict::getSortOrder);
        
        return list(wrapper);
    }
    
    @Override
    public List<String> getAllDictTypes() {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysDict::getDictType)
                .groupBy(SysDict::getDictType)
                .orderByAsc(SysDict::getDictType);
        
        List<SysDict> dicts = list(wrapper);
        return dicts.stream()
                .map(SysDict::getDictType)
                .collect(Collectors.toList());
    }
}
