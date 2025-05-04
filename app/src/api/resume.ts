import request from '@/utils/request'

export interface Resume {
  id: number
  name: string
  url: string
  uploadTime: string
  fileSize: number
  status: 'analyzing' | 'analyzed' | 'error'
  analysis?: {
    summary: string
    skills: string[]
    experience: string[]
    education: string[]
  }
}

// 获取简历列表
export const getResumeList = () => {
  return request({
    url: '/api/resumes',
    method: 'get'
  })
}

// 上传简历
export const uploadResume = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/api/resumes/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除简历
export const deleteResume = (id: number) => {
  return request({
    url: `/api/resumes/${id}`,
    method: 'delete'
  })
}

// 获取简历分析结果
export const getResumeAnalysis = (id: number) => {
  return request({
    url: `/api/resumes/${id}/analysis`,
    method: 'get'
  })
} 