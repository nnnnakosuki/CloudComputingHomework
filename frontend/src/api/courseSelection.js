import request from '@/utils/request'

// 获取选课记录列表
export const getCourseSelectionList = (params) => {
  return request({
    url: '/course-selections',
    method: 'get',
    params
  })
}

// 学生选课
export const selectCourse = (data) => {
  return request({
    url: '/course-selections/select',
    method: 'post',
    data
  })
}

// 学生退课
export const dropCourse = (id) => {
  return request({
    url: `/course-selections/${id}/drop`,
    method: 'post'
  })
}

// 批量确认选课
export const batchConfirmSelections = (selectionIds) => {
  return request({
    url: '/course-selections/batch-confirm',
    method: 'post',
    data: selectionIds
  })
}

// 批量拒绝选课
export const batchRejectSelections = (selectionIds) => {
  return request({
    url: '/course-selections/batch-reject',
    method: 'post',
    data: selectionIds
  })
}

// 获取当前学生选课列表
export const getMySelections = (semester) => {
  return request({
    url: '/course-selections/my-selections',
    method: 'get',
    params: { semester }
  })
}

// 获取指定学生选课列表（管理员和教师使用）
export const getStudentSelections = (studentId, semester, courseScheduleId = null) => {
  if (!studentId || studentId === 'null' || studentId === null) {
    return Promise.reject(new Error('学生ID不能为空'))
  }
  return request({
    url: `/course-selections/student/${studentId}`,
    method: 'get',
    params: { semester, courseScheduleId }
  })
}

// 取消选课
export const cancelCourseSelection = (selectionId) => {
  return request({
    url: `/course-selections/${selectionId}/cancel`,
    method: 'post'
  })
}