package com.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.dto.StudentInfoDTO;
import com.course.dto.StudentStatusChangeDTO;
import com.course.entity.StudentInfo;
import com.course.entity.StudentStatus;
import com.course.entity.SysUser;
import com.course.exception.BusinessException;
import com.course.mapper.StudentInfoMapper;
import com.course.mapper.StudentStatusMapper;
import com.course.service.StudentInfoService;
import com.course.service.SysUserService;
import com.course.vo.StudentInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生信息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {
    
    private final SysUserService sysUserService;
    private final StudentStatusMapper studentStatusMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public IPage<StudentInfoVO> getStudentPage(IPage<StudentInfo> page, String keyword, Long deptId, Integer status) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(StudentInfo::getStudentNo, keyword)
                    .or().like(StudentInfo::getName, keyword));
        }
        
        if (deptId != null) {
            wrapper.eq(StudentInfo::getDeptId, deptId);
        }
        
        if (status != null) {
            wrapper.eq(StudentInfo::getStatus, status);
        }
        
        wrapper.orderByDesc(StudentInfo::getCreateTime);
        
        IPage<StudentInfo> studentPage = page(page, wrapper);
        
        // 转换为VO
        IPage<StudentInfoVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<StudentInfoVO> voList = studentPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStudent(StudentInfoDTO studentInfoDTO) {
        // 检查学号是否已存在
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentNo, studentInfoDTO.getStudentNo());
        if (count(wrapper) > 0) {
            throw new BusinessException("学号已存在");
        }
        
        // 检查用户名是否已存在
        SysUser existUser = sysUserService.getByUsername(studentInfoDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户账号
        SysUser user = new SysUser();
        user.setUsername(studentInfoDTO.getUsername());
        user.setPassword(passwordEncoder.encode(studentInfoDTO.getPassword()));
        user.setRealName(studentInfoDTO.getRealName());
        user.setRoleId(4L); // 学生角色
        user.setDeptId(studentInfoDTO.getDeptId());
        user.setStatus(1);
        
        boolean userSaved = sysUserService.save(user);
        if (!userSaved) {
            throw new BusinessException("创建用户账号失败");
        }
        
        // 创建学生信息
        StudentInfo studentInfo = new StudentInfo();
        BeanUtils.copyProperties(studentInfoDTO, studentInfo);
        studentInfo.setUserId(user.getId());
        studentInfo.setStatus(1); // 在校状态
        
        boolean studentSaved = save(studentInfo);
        if (!studentSaved) {
            throw new BusinessException("创建学生信息失败");
        }
        
        // 记录学籍变动
        StudentStatus status = new StudentStatus();
        status.setStudentId(studentInfo.getId());
        status.setStatusType(1); // 注册
        status.setReason("新生注册");
        status.setOperatorId(1L); // 系统管理员
        status.setOperateTime(LocalDateTime.now());
        studentStatusMapper.insert(status);
        
        log.info("成功添加学生: {}", studentInfoDTO.getStudentNo());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(StudentInfoDTO studentInfoDTO) {
        StudentInfo existStudent = getById(studentInfoDTO.getId());
        if (existStudent == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 检查学号是否被其他学生使用
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentNo, studentInfoDTO.getStudentNo())
                .ne(StudentInfo::getId, studentInfoDTO.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException("学号已被其他学生使用");
        }
        
        // 更新学生信息 - 使用精确更新避免enrollment_year字段问题
        LambdaUpdateWrapper<StudentInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StudentInfo::getId, studentInfoDTO.getId())
                    .set(StudentInfo::getStudentNo, studentInfoDTO.getStudentNo())
                    .set(StudentInfo::getName, studentInfoDTO.getName())
                    .set(StudentInfo::getGender, studentInfoDTO.getGender())
                    .set(StudentInfo::getDeptId, studentInfoDTO.getDeptId())
                    .set(StudentInfo::getMajor, studentInfoDTO.getMajor())
                    .set(StudentInfo::getClassName, studentInfoDTO.getClassName())
                    .set(StudentInfo::getEnrollmentYear, studentInfoDTO.getEnrollmentYear())
                    .set(StudentInfo::getStatus, studentInfoDTO.getStatus());
        
        boolean updated = update(updateWrapper);
        if (!updated) {
            throw new BusinessException("更新学生信息失败");
        }
        
        // 更新用户信息
        SysUser user = sysUserService.getById(existStudent.getUserId());
        if (user != null) {
            user.setRealName(studentInfoDTO.getRealName());
            user.setDeptId(studentInfoDTO.getDeptId());
            sysUserService.updateById(user);
        }
        
        log.info("成功更新学生信息: {}", studentInfoDTO.getStudentNo());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(Long id) {
        StudentInfo student = getById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 删除学生信息
        boolean deleted = removeById(id);
        if (!deleted) {
            throw new BusinessException("删除学生信息失败");
        }
        
        // 删除用户账号
        if (student.getUserId() != null) {
            sysUserService.removeById(student.getUserId());
        }
        
        log.info("成功删除学生: {}", student.getStudentNo());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStudentStatus(StudentStatusChangeDTO statusChangeDTO) {
        StudentInfo student = getById(statusChangeDTO.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 只更新学生状态，避免更新其他字段
        LambdaUpdateWrapper<StudentInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StudentInfo::getId, statusChangeDTO.getStudentId())
                    .set(StudentInfo::getStatus, statusChangeDTO.getStatusType());
        boolean updated = update(updateWrapper);
        if (!updated) {
            throw new BusinessException("更新学生状态失败");
        }
        
        // 记录学籍变动
        StudentStatus status = new StudentStatus();
        status.setStudentId(statusChangeDTO.getStudentId());
        status.setStatusType(statusChangeDTO.getStatusType());
        status.setReason(statusChangeDTO.getReason());
        status.setOperatorId(1L); // 当前操作员ID，实际应该从SecurityContext获取
        status.setOperateTime(LocalDateTime.now());
        studentStatusMapper.insert(status);
        
        log.info("成功处理学籍变动: 学生ID={}, 变动类型={}", statusChangeDTO.getStudentId(), statusChangeDTO.getStatusType());
        return true;
    }
    
    @Override
    public StudentInfo getByUserId(Long userId) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getUserId, userId);
        return getOne(wrapper);
    }
    
    /**
     * 转换为VO
     */
    private StudentInfoVO convertToVO(StudentInfo studentInfo) {
        StudentInfoVO vo = new StudentInfoVO();
        BeanUtils.copyProperties(studentInfo, vo);
        
        // 设置性别文本
        vo.setGenderText(studentInfo.getGender() == 1 ? "男" : "女");
        
        // 设置状态文本
        switch (studentInfo.getStatus()) {
            case 1:
                vo.setStatusText("在校");
                break;
            case 2:
                vo.setStatusText("休学");
                break;
            case 3:
                vo.setStatusText("退学");
                break;
            case 4:
                vo.setStatusText("毕业");
                break;
            default:
                vo.setStatusText("未知");
        }
        
        // 获取用户信息
        if (studentInfo.getUserId() != null) {
            SysUser user = sysUserService.getById(studentInfo.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setRealName(user.getRealName());
            }
        }
        
        return vo;
    }
}
