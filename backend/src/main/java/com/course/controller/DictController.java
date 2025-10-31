package com.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.course.common.Result;
import com.course.dto.DictDTO;
import com.course.entity.SysDict;
import com.course.service.SysDictService;
import com.course.vo.DictVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/dict")
@RequiredArgsConstructor
public class DictController {
    
    private final SysDictService sysDictService;
    
    /**
     * 分页查询数据字典列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<DictVO>> getDictPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dictType) {
        
        try {
            Page<SysDict> page = new Page<>(current, size);
            IPage<SysDict> dictPage = sysDictService.getDictPage(page, keyword, dictType);
            
            // 转换为VO
            IPage<DictVO> result = dictPage.convert(this::convertToVO);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取数据字典列表失败", e);
            return Result.error("获取数据字典列表失败");
        }
    }
    
    /**
     * 根据ID获取数据字典信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<DictVO> getDictById(@PathVariable Long id) {
        try {
            SysDict dict = sysDictService.getById(id);
            if (dict == null) {
                return Result.error("数据字典不存在");
            }
            
            DictVO dictVO = convertToVO(dict);
            return Result.success(dictVO);
        } catch (Exception e) {
            log.error("获取数据字典信息失败", e);
            return Result.error("获取数据字典信息失败");
        }
    }
    
    /**
     * 添加数据字典
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addDict(@Valid @RequestBody DictDTO dictDTO) {
        try {
            // 检查字典类型和值是否已存在
            LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDict::getDictType, dictDTO.getDictType())
                    .eq(SysDict::getDictValue, dictDTO.getDictValue());
            SysDict existingDict = sysDictService.getOne(wrapper);
            if (existingDict != null) {
                return Result.error("该字典类型下的字典值已存在");
            }
            
            SysDict dict = new SysDict();
            dict.setDictType(dictDTO.getDictType());
            dict.setDictLabel(dictDTO.getDictLabel());
            dict.setDictValue(dictDTO.getDictValue());
            dict.setSortOrder(dictDTO.getSortOrder() != null ? dictDTO.getSortOrder() : 0);
            dict.setStatus(dictDTO.getStatus() != null ? dictDTO.getStatus() : 1);
            dict.setRemark(dictDTO.getRemark());
            
            boolean success = sysDictService.save(dict);
            if (success) {
                log.info("添加数据字典成功: {} - {}", dictDTO.getDictType(), dictDTO.getDictValue());
                return Result.success();
            } else {
                return Result.error("添加数据字典失败");
            }
        } catch (Exception e) {
            log.error("添加数据字典失败", e);
            return Result.error("添加数据字典失败");
        }
    }
    
    /**
     * 更新数据字典信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateDict(@PathVariable Long id, @Valid @RequestBody DictDTO dictDTO) {
        try {
            SysDict dict = sysDictService.getById(id);
            if (dict == null) {
                return Result.error("数据字典不存在");
            }
            
            // 检查字典类型和值是否被其他字典使用
            LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDict::getDictType, dictDTO.getDictType())
                    .eq(SysDict::getDictValue, dictDTO.getDictValue())
                    .ne(SysDict::getId, id);
            SysDict existingDict = sysDictService.getOne(wrapper);
            if (existingDict != null) {
                return Result.error("该字典类型下的字典值已被其他字典使用");
            }
            
            dict.setDictType(dictDTO.getDictType());
            dict.setDictLabel(dictDTO.getDictLabel());
            dict.setDictValue(dictDTO.getDictValue());
            dict.setSortOrder(dictDTO.getSortOrder());
            dict.setStatus(dictDTO.getStatus());
            dict.setRemark(dictDTO.getRemark());
            
            boolean success = sysDictService.updateById(dict);
            if (success) {
                log.info("更新数据字典成功: {} - {}", dictDTO.getDictType(), dictDTO.getDictValue());
                return Result.success();
            } else {
                return Result.error("更新数据字典失败");
            }
        } catch (Exception e) {
            log.error("更新数据字典失败", e);
            return Result.error("更新数据字典失败");
        }
    }
    
    /**
     * 删除数据字典
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDict(@PathVariable Long id) {
        try {
            SysDict dict = sysDictService.getById(id);
            if (dict == null) {
                return Result.error("数据字典不存在");
            }
            
            boolean success = sysDictService.removeById(id);
            if (success) {
                log.info("删除数据字典成功: {} - {}", dict.getDictType(), dict.getDictValue());
                return Result.success();
            } else {
                return Result.error("删除数据字典失败");
            }
        } catch (Exception e) {
            log.error("删除数据字典失败", e);
            return Result.error("删除数据字典失败");
        }
    }
    
    /**
     * 根据字典类型获取字典列表
     */
    @GetMapping("/type/{dictType}")
    public Result<List<DictVO>> getDictByType(@PathVariable String dictType) {
        try {
            List<SysDict> dicts = sysDictService.getDictByType(dictType);
            List<DictVO> dictVOs = dicts.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            
            return Result.success(dictVOs);
        } catch (Exception e) {
            log.error("获取字典列表失败", e);
            return Result.error("获取字典列表失败");
        }
    }
    
    /**
     * 获取所有字典类型
     */
    @GetMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<String>> getAllDictTypes() {
        try {
            List<String> dictTypes = sysDictService.getAllDictTypes();
            return Result.success(dictTypes);
        } catch (Exception e) {
            log.error("获取字典类型列表失败", e);
            return Result.error("获取字典类型列表失败");
        }
    }
    
    /**
     * 转换为VO对象
     */
    private DictVO convertToVO(SysDict dict) {
        DictVO vo = new DictVO();
        vo.setId(dict.getId());
        vo.setDictType(dict.getDictType());
        vo.setDictLabel(dict.getDictLabel());
        vo.setDictValue(dict.getDictValue());
        vo.setSortOrder(dict.getSortOrder());
        vo.setStatus(dict.getStatus());
        vo.setStatusText(dict.getStatus() == 1 ? "正常" : "禁用");
        vo.setRemark(dict.getRemark());
        return vo;
    }
}
