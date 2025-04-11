import request from './request'

// 上传简历
export function uploadResume(data) {
  return request({
    url: '/resume/upload',
    method: 'post',
    data
  })
}

// 获取简历解析结果
export function getResumeAnalysis(id) {
  return request({
    url: `/resume/${id}`,
    method: 'get'
  })
}

// 更新简历
export function updateResume(data) {
  return request({
    url: '/resume/update',
    method: 'put',
    data
  })
}

// 获取优化建议
export function getResumeSuggestions() {
  return request({
    url: '/resume/suggestions',
    method: 'get'
  })
}

// 导出简历
export function exportResume(data) {
  return request({
    url: '/resume/export',
    method: 'post',
    data,
    responseType: 'blob'
  })
}