import { API_CONFIG } from '../config'
import axios, { InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'

// 使用配置文件中的BASE_URL
const BASE_URL = API_CONFIG.BASE_URL

// 创建axios实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: API_CONFIG.TIMEOUT
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  (error: AxiosError) => {
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

export interface SendMessageParams {
  question?: string;
  sessionId: string;
  file?: File;
}

// 发送消息
export const sendMessage = async ({ question, sessionId, file }: SendMessageParams) => {
  try {
    // 创建 FormData 对象
    const formData = new FormData()

    // 添加必要的参数
    formData.append('sessionId', sessionId)

    // 添加可选参数
    if (question) {
      formData.append('question', question)
    }
    if (file) {
      formData.append('file', file)
    }

    const response = await request.post('/api/chat/message',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Accept': 'application/json'
        }
      }
    )
    return response.data
  } catch (error) {
    console.error('发送消息失败:', error)
    throw error
  }
}

// 获取历史消息
export const getChatHistory = async (params: {
  userId: number
  sessionId?: string
}): Promise<ChatMessage[]> => {
  try {
    const response = await request.get(
      '/api/chat/history',
      {
        params: {
          sessionId: params.sessionId || undefined
        },
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      }
    )
    return response.data
  } catch (error) {
    console.error('获取历史消息失败:', error)
    throw error
  }
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