package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.GradeImportDTO;
import com.course.dto.GradeInputDTO;
import com.course.entity.CourseGrade;
import com.course.vo.CourseGradeVO;

import java.util.List;

/**
 * 成绩服务接口
 */
public interface CourseGradeService extends IService<CourseGrade> {
    
    /**
     * 分页查询成绩信息
     */
    IPage<CourseGradeVO> getGradePage(IPage<CourseGrade> page, Long studentId, Long courseScheduleId, Integer auditStatus);
    
    /**
     * 教师录入成绩
     */
    boolean inputGrade(GradeInputDTO gradeInputDTO);
    
    /**
     * 批量录入成绩
     */
    boolean batchInputGrades(Long courseScheduleId, java.util.List<GradeInputDTO> gradeList);
    
    /**
     * 审核成绩
     */
    boolean auditGrade(Long gradeId, Integer auditStatus, String remark);
    
    /**
     * 根据学生ID和开课ID获取成绩
     */
    CourseGrade getByStudentAndCourse(Long studentId, Long courseScheduleId);
    
    /**
     * 批量导入成绩（从Excel/CSV）
     */
    boolean importGrades(List<GradeImportDTO> importList);
    
    /**
     * 设置成绩查询时段
     */
    boolean setQueryTime(Long courseScheduleId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    
    /**
     * 检查是否在查询时段内
     */
    boolean checkQueryTime(CourseGrade grade);
}
