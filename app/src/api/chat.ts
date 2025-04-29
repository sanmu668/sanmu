import request from '@/utils/request'

export interface ChatRequestDTO {
  message: string
  sessionId: string
}

export interface ChatMessage {
  id: string
  sessionId: string
  content: string
  type: 'user' | 'ai'
  timestamp: string
}

export const sendMessage = (data: ChatRequestDTO): Promise<ChatMessage> => {
  return request({
    url: '/chat/message',
    method: 'post',
    data
  })
}

export const createNewSession = (): Promise<ChatMessage> => {
  return request({
    url: '/chat/new-session',
    method: 'post'
  })
} 