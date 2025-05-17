import axios from 'axios'

// 使用与简历表相同的配置
const BASE_URL = 'http://localhost:8080'

// 创建axios实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  timeoutErrorMessage: '请求超时，请检查网络连接或稍后重试'
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
      config.headers['Content-Type'] = config.headers['Content-Type'] || 'application/json'
      config.headers['accept'] = '*/*'
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
      error.message = '请求超时，请检查网络连接或稍后重试'
    }
    return Promise.reject(error)
  }
)

export interface ChatMessage {
  id: number
  userId: number
  sessionId: string
  question?: string
  answer?: string
  content?: string
  type?: 'user' | 'ai'
  timestamp: string
  time?: string
}

export interface ChatSession {
  id: string
  title: string
  lastMessage: string
  timestamp: string
}

// 发送消息
export const sendMessage = async (params: {
  userId: number
  sessionId: string
  question?: string
  file?: File
}): Promise<ChatMessage> => {
  const formData = new FormData()
  formData.append('sessionId', params.sessionId)

  if (params.question) {
    formData.append('question', params.question)
  }

  if (params.file) {
    formData.append('file', params.file)
  }

  const response = await request.post('/api/chat/message', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'accept': '*/*'
    }
  })
  return response.data
}

// 获取历史消息
export const getChatHistory = async (params: {
  userId: number
  sessionId: string
}): Promise<ChatMessage[]> => {
  const response = await request.get(
    '/api/chat/history',
    {
      params,
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json',
        'accept': '*/*'
      }
    }
  )
  return response.data
}

// 创建新会话
export const createNewSession = async (): Promise<ChatMessage> => {
  const response = await request.post('/api/chat/new-session', null, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json',
      'accept': '*/*'
    }
  })
  return response.data
} 