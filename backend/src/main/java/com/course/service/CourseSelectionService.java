package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.CourseSelectionDTO;
import com.course.entity.CourseSelection;
import com.course.vo.CourseSelectionVO;

import java.util.List;

/**
 * 选课服务接口
 */
public interface CourseSelectionService extends IService<CourseSelection> {
    
    /**
     * 分页查询选课记录
     */
    IPage<CourseSelectionVO> getSelectionPage(IPage<CourseSelection> page, Long studentId, Long courseScheduleId, Integer status);
    
    /**
     * 学生选课
     */
    boolean selectCourse(CourseSelectionDTO selectionDTO);
    
    /**
     * 学生退课
     */
    boolean dropCourse(Long selectionId);
    
    /**
     * 批量确认选课
     */
    boolean batchConfirmSelections(List<Long> selectionIds);
    
    /**
     * 批量拒绝选课
     */
    boolean batchRejectSelections(List<Long> selectionIds);
    
    /**
     * 获取学生选课列表
     */
    List<CourseSelectionVO> getStudentSelections(Long studentId, String semester);
    
    /**
     * 检查选课容量
     */
    boolean checkCourseCapacity(Long courseScheduleId);
}
