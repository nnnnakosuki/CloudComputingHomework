package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.CourseDTO;
import com.course.entity.Course;
import com.course.vo.CourseVO;

/**
 * 课程信息服务接口
 */
public interface CourseService extends IService<Course> {
    
    /**
     * 分页查询课程信息
     */
    IPage<CourseVO> getCoursePage(IPage<Course> page, String keyword, Long deptId, Integer courseType);
    
    /**
     * 添加课程
     */
    boolean addCourse(CourseDTO courseDTO);
    
    /**
     * 更新课程信息
     */
    boolean updateCourse(CourseDTO courseDTO);
    
    /**
     * 删除课程
     */
    boolean deleteCourse(Long id);
    
    /**
     * 启用/禁用课程
     */
    boolean changeCourseStatus(Long id, Integer status);
    
    /**
     * 根据课程编号获取课程
     */
    Course getByCourseCode(String courseCode);
}
