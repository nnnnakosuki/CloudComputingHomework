import request from '@/utils/request'

// 获取成绩列表
export const getGradeList = (params) => {
  return request({
    url: '/grades',
    method: 'get',
    params
  })
}

// 录入成绩
export const inputGrade = (data) => {
  return request({
    url: '/grades/input',
    method: 'post',
    data
  })
}

// 批量录入成绩
export const batchInputGrades = (courseScheduleId, data) => {
  return request({
    url: '/grades/batch-input',
    method: 'post',
    params: { courseScheduleId },
    data
  })
}

// 审核成绩
export const auditGrade = (id, auditStatus, remark) => {
  return request({
    url: `/grades/${id}/audit`,
    method: 'post',
    params: { auditStatus, remark }
  })
}

// 根据学生ID获取成绩
export const getGradesByStudent = (studentId) => {
  return request({
    url: `/grades/student/${studentId}`,
    method: 'get'
  })
}

// 导入成绩（Excel/CSV）- 直接使用Element Plus上传，不需要此接口

// 设置成绩查询时段
export const setQueryTime = (courseScheduleId, startTime, endTime) => {
  return request({
    url: '/grades/query-time',
    method: 'post',
    params: { courseScheduleId, startTime, endTime }
  })
}

// 获取成绩列表（教师查看）
export const getTeacherGradeList = (params) => {
  return request({
    url: '/grades',
    method: 'get',
    params
  })
}

// 删除成绩
export const deleteGrade = (id) => {
  return request({
    url: `/grades/${id}`,
    method: 'delete'
  })
}

// 更新成绩
export const updateGrade = (id, data) => {
  return request({
    url: `/grades/${id}`,
    method: 'put',
    data
  })
}