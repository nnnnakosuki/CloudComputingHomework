import request from '@/utils/request'

// 获取教学计划列表
export const getTeachingPlanList = (params) => {
  return request({
    url: '/teaching-plans',
    method: 'get',
    params
  })
}

// 获取教学计划详情
export const getTeachingPlanById = (id) => {
  return request({
    url: `/teaching-plans/${id}`,
    method: 'get'
  })
}

// 添加教学计划
export const addTeachingPlan = (data) => {
  return request({
    url: '/teaching-plans',
    method: 'post',
    data
  })
}

// 更新教学计划
export const updateTeachingPlan = (id, data) => {
  return request({
    url: `/teaching-plans/${id}`,
    method: 'put',
    data
  })
}

// 删除教学计划
export const deleteTeachingPlan = (id) => {
  return request({
    url: `/teaching-plans/${id}`,
    method: 'delete'
  })
}

// 启用/禁用教学计划
export const changeTeachingPlanStatus = (id, status) => {
  return request({
    url: `/teaching-plans/${id}/status`,
    method: 'post',
    params: { status }
  })
}
