import axios from 'axios'

// 使用相对路径，通过 Vite 代理转发请求
const BASE_URL = '/api'

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
  question: string
}): Promise<ChatMessage> => {
  const response = await axios.post(`${BASE_URL}/chat/message`, params)
  return response.data
}

// 获取历史消息
export const getChatHistory = async (params: {
  userId: number
  sessionId: string
}): Promise<ChatMessage[]> => {
  const response = await axios.get(
    `${BASE_URL}/chat/history`,
    { params }
  )
  return response.data
}

// 创建新会话
export const createNewSession = async (): Promise<ChatMessage> => {
  // TODO: Implement when backend provides this endpoint
  return {
    id: Date.now(),
    userId: 1,
    sessionId: Math.random().toString(36).substring(7),
    answer: "您好！我是您的AI简历优化与职业咨询专家。请问有什么可以帮您？",
    timestamp: new Date().toISOString(),
    type: 'ai'
  }
} 