import request from '@/utils/request'

// 获取排课列表
export const getClassScheduleList = (params) => {
  return request({
    url: '/class-schedules',
    method: 'get',
    params
  })
}

// 添加排课
export const addClassSchedule = (data) => {
  return request({
    url: '/class-schedules',
    method: 'post',
    data
  })
}

// 更新排课
export const updateClassSchedule = (id, data) => {
  return request({
    url: `/class-schedules/${id}`,
    method: 'put',
    data
  })
}

// 删除排课
export const deleteClassSchedule = (id) => {
  return request({
    url: `/class-schedules/${id}`,
    method: 'delete'
  })
}

// 批量排课
export const batchAddClassSchedules = (data) => {
  return request({
    url: '/class-schedules/batch',
    method: 'post',
    data
  })
}

// 检查时间冲突
export const checkTimeConflict = (data) => {
  return request({
    url: '/class-schedules/check-conflict',
    method: 'post',
    data
  })
}

// 获取周课表
export const getWeeklySchedule = (semester, weekDay) => {
  return request({
    url: '/class-schedules/weekly',
    method: 'get',
    params: { semester, weekDay }
  })
}

// 获取教师课表
export const getTeacherSchedule = (teacherId, semester) => {
  return request({
    url: `/class-schedules/teacher/${teacherId}`,
    method: 'get',
    params: { semester }
  })
}

// 获取学生课表
export const getStudentSchedule = (studentId, semester) => {
  return request({
    url: `/class-schedules/student/${studentId}`,
    method: 'get',
    params: { semester }
  })
}
