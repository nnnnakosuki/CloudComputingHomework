import request from '@/utils/request'

// 获取调课记录列表
export const getScheduleAdjustmentList = (params) => {
  return request({
    url: '/schedule-adjustments',
    method: 'get',
    params
  })
}

// 申请调课
export const applyScheduleAdjustment = (data) => {
  return request({
    url: '/schedule-adjustments/apply',
    method: 'post',
    data
  })
}

// 审批调课
export const approveScheduleAdjustment = (id, status, remark) => {
  return request({
    url: `/schedule-adjustments/${id}/approve`,
    method: 'post',
    params: { status, remark }
  })
}

// 获取待审批的调课申请
export const getPendingAdjustments = () => {
  return request({
    url: '/schedule-adjustments/pending',
    method: 'get'
  })
}

// 执行调课
export const executeScheduleAdjustment = (id) => {
  return request({
    url: `/schedule-adjustments/${id}/execute`,
    method: 'post'
  })
}
