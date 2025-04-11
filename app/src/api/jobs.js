import request from './request'

// 获取推荐职位
export function getRecommendJobs() {
  return request({
    url: '/jobs/recommend',
    method: 'get'
  })
}

// 申请职位
export function applyJob(id) {
  return request({
    url: `/jobs/apply/${id}`,
    method: 'post'
  })
}

// 获取职位详情
export function getJobDetail(id) {
  return request({
    url: `/jobs/${id}`,
    method: 'get'
  })
}

// 获取热门职位
export function getTrendingJobs() {
  return request({
    url: '/jobs/trending',
    method: 'get'
  })
}