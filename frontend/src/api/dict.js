import request from '@/utils/request'

// 获取数据字典列表（分页）
export function getDictList(params) {
  return request({
    url: '/dict',
    method: 'get',
    params
  })
}

// 根据ID获取数据字典信息
export function getDictById(id) {
  return request({
    url: `/dict/${id}`,
    method: 'get'
  })
}

// 添加数据字典
export function addDict(data) {
  return request({
    url: '/dict',
    method: 'post',
    data
  })
}

// 更新数据字典信息
export function updateDict(id, data) {
  return request({
    url: `/dict/${id}`,
    method: 'put',
    data
  })
}

// 删除数据字典
export function deleteDict(id) {
  return request({
    url: `/dict/${id}`,
    method: 'delete'
  })
}

// 根据字典类型获取字典列表
export function getDictByType(dictType) {
  return request({
    url: `/dict/type/${dictType}`,
    method: 'get'
  })
}

// 获取所有字典类型
export function getAllDictTypes() {
  return request({
    url: '/dict/types',
    method: 'get'
  })
}
