package com.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.StudentInfoDTO;
import com.course.dto.StudentStatusChangeDTO;
import com.course.entity.StudentInfo;
import com.course.vo.StudentInfoVO;

/**
 * 学生信息服务接口
 */
public interface StudentInfoService extends IService<StudentInfo> {
    
    /**
     * 分页查询学生信息
     */
    IPage<StudentInfoVO> getStudentPage(IPage<StudentInfo> page, String keyword, Long deptId, Integer status);
    
    /**
     * 添加学生
     */
    boolean addStudent(StudentInfoDTO studentInfoDTO);
    
    /**
     * 更新学生信息
     */
    boolean updateStudent(StudentInfoDTO studentInfoDTO);
    
    /**
     * 删除学生
     */
    boolean deleteStudent(Long id);
    
    /**
     * 学籍变动处理
     */
    boolean changeStudentStatus(StudentStatusChangeDTO statusChangeDTO);
    
    /**
     * 根据用户ID获取学生信息
     */
    StudentInfo getByUserId(Long userId);
}
