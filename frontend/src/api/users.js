import request from '@/utils/request'

// 获取用户列表（分页）
export function getUserList(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

// 获取所有用户列表
export function getAllUsers() {
  return request({
    url: '/users/all',
    method: 'get'
  })
}

// 根据ID获取用户信息
export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

// 添加用户
export function addUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}
