import request from '@/utils/request'

// 获取学生列表
export const getStudentList = (params) => {
  return request({
    url: '/students',
    method: 'get',
    params
  })
}

// 获取学生详情
export const getStudentById = (id) => {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

// 添加学生
export const addStudent = (data) => {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

// 更新学生信息
export const updateStudent = (id, data) => {
  return request({
    url: `/students/${id}`,
    method: 'put',
    data
  })
}

// 删除学生
export const deleteStudent = (id) => {
  return request({
    url: `/students/${id}`,
    method: 'delete'
  })
}

// 学籍变动
export const changeStudentStatus = (id, data) => {
  return request({
    url: `/students/${id}/status`,
    method: 'post',
    data
  })
}
