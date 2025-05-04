import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

export interface Job {
  id: number
  jobName: string
  department: string
  salaryRange: string
  experience: string
  education: string
  jobType: string
  description: string
  requirements: string
  company: string
  location: string
  tags: string
  status: string
  createdAt: string
  updatedAt: string
}

export interface JobForm {
  jobName: string
  department: string
  salaryRange: string
  experience: string
  education: string
  jobType: string
  description: string
  requirements: string
  company: string
  location: string
  tags: string
  status: string
}

export interface JobWithArrayTags extends Omit<Job, 'tags'> {
  tags: string[]
}

export interface JobQueryParams {
  page?: number
  pageSize?: number
  jobName?: string
  company?: string
  status?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

// 获取职位列表
export function getJobList(params: JobQueryParams): Promise<PageResponse<Job>> {
  // 转换参数名称以匹配后端API
  const apiParams = {
    ...params,
    pagesize: params.pageSize // 后端接收 pagesize 而不是 pageSize
  }
  delete apiParams.pageSize

  return request<PageResponse<Job>>({
    url: '/api/admin/job',
    method: 'get',
    params: apiParams
  })
}

// 创建职位
export function createJob(data: JobForm): Promise<Job> {
  return request<Job>({
    url: '/api/admin/job',
    method: 'post',
    data
  })
}

// 更新职位
export function updateJob(id: number, data: JobForm): Promise<Job> {
  return request<Job>({
    url: `/api/admin/job/${id}`,
    method: 'put',
    data
  })
}

// 删除职位
export function deleteJob(id: number): Promise<void> {
  return request({
    url: `/api/admin/job/${id}`,
    method: 'delete'
  })
}

// 更新职位状态
export function updateJobStatus(id: number, status: string): Promise<Job> {
  return request<Job>({
    url: `/api/admin/job/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取职位详情
export function getJobDetail(id: number): Promise<Job> {
  return request<Job>({
    url: `/api/admin/job/${id}`,
    method: 'get'
  })
} 