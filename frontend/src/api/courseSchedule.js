import request from '@/utils/request'

// 获取开课计划列表
export const getCourseScheduleList = (params) => {
  return request({
    url: '/course-schedules',
    method: 'get',
    params
  })
}

// 发布开课计划
export const publishCourseSchedule = (data) => {
  return request({
    url: '/course-schedules',
    method: 'post',
    data
  })
}

// 更新开课计划
export const updateCourseSchedule = (id, data) => {
  return request({
    url: `/course-schedules/${id}`,
    method: 'put',
    data
  })
}

// 删除开课计划
export const deleteCourseSchedule = (id) => {
  return request({
    url: `/course-schedules/${id}`,
    method: 'delete'
  })
}

// 启用/禁用开课计划
export const changeCourseScheduleStatus = (id, status) => {
  return request({
    url: `/course-schedules/${id}/status`,
    method: 'post',
    params: { status }
  })
}

// 获取可选课程列表
export const getAvailableCourses = (studentId, semester) => {
  return request({
    url: '/course-schedules/available',
    method: 'get',
    params: { studentId, semester }
  })
}
