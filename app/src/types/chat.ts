export interface ChatMessage {
  content: string
  type: 'user' | 'ai'
  time: string
  id?: string
  sessionId?: string
}

export interface ChatRequestDTO {
  message: string
  sessionId: string
}

export interface ChatResponse {
  id: string
  sessionId: string
  content: string
  type: 'user' | 'ai'
  timestamp: string
}

export interface ChatSession {
  id: string
  title: string
  lastMessage: string
  timestamp: string
} 