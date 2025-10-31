import request from '@/utils/request'

// 获取角色列表（分页）
export function getRoleList(params) {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

// 获取所有角色列表
export function getAllRoles() {
  return request({
    url: '/roles/all',
    method: 'get'
  })
}

// 根据ID获取角色信息
export function getRoleById(id) {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

// 添加角色
export function addRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

// 更新角色信息
export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}
