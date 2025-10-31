import request from '@/utils/request'

// 获取课程列表
export const getCourseList = (params) => {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

// 获取课程详情
export const getCourseById = (id) => {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  })
}

// 添加课程
export const addCourse = (data) => {
  return request({
    url: '/courses',
    method: 'post',
    data
  })
}

// 更新课程信息
export const updateCourse = (id, data) => {
  return request({
    url: `/courses/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export const deleteCourse = (id) => {
  return request({
    url: `/courses/${id}`,
    method: 'delete'
  })
}

// 启用/禁用课程
export const changeCourseStatus = (id, status) => {
  return request({
    url: `/courses/${id}/status`,
    method: 'post',
    params: { status }
  })
}

// 获取所有课程列表
export const getAllCourses = () => {
  return request({
    url: '/courses/all',
    method: 'get'
  })
}
