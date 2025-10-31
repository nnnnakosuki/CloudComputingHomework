import request from '@/utils/request'

// 获取当前学生个人信息
export function getCurrentStudentProfile() {
  return request({
    url: '/student/profile',
    method: 'get'
  })
}

// 更新当前学生个人信息
export function updateCurrentStudentProfile(data) {
  return request({
    url: '/student/profile',
    method: 'put',
    data
  })
}
