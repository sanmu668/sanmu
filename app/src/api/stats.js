import request from './request'

// 获取行业趋势
export function getTrends() {
  return request({
    url: '/stats/trends',
    method: 'get'
  })
}

// 获取薪资分析
export function getSalaryAnalysis() {
  return request({
    url: '/stats/salary',
    method: 'get'
  })
}

// 获取热门技能
export function getHotSkills() {
  return request({
    url: '/stats/skills',
    method: 'get'
  })
}