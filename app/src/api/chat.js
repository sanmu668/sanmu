import request from './request'

export const createNewSession = async () => {
  try {
    const response = await request({
      url: '/chat/new-session',
      method: 'post'
    })
    localStorage.setItem('chatSessionId', response.data.sessionId)
    return response.data.sessionId
  } catch (error) {
    console.error('创建会话失败:', error)
    throw new Error('无法创建新会话')
  }
}

// 发送聊天消息（REST API方式）
export const sendChatMessage = async (message, sessionId, retryCount = 0) => {
  try {
    console.log('Sending message:', { message, sessionId })
    const response = await request({
      url: '/chat/message',
      method: 'post',
      data: { message, sessionId }
    })
    console.log('API Response:', response)

    // 检查响应格式
    if (!response) {
      throw new Error('服务器响应为空')
    }

    // 如果响应直接包含数据，返回响应本身
    if (response.content && typeof response.content === 'string') {
      return response
    }

    // 如果响应包含在data字段中
    if (response.data && typeof response.data.content === 'string') {
      return response.data
    }

    console.error('Unexpected response format:', response)
    throw new Error('服务器响应格式错误')
  } catch (error) {
    console.error('发送消息失败:', error)

    // 如果是超时错误且未超过最大重试次数，则重试
    if (error.code === 'ECONNABORTED' &&
      error.message.includes('timeout') &&
      retryCount < 2) {
      console.log(`Retrying request (attempt ${retryCount + 1})...`)
      return sendChatMessage(message, sessionId, retryCount + 1)
    }

    throw error
  }
}

// 发送消息（统一REST风格）
export const sendMessage = (data) => {
  if (!data.sessionId) {
    data.sessionId = localStorage.getItem('chatSessionId') || Date.now().toString()
  }
  return request({
    url: '/chat/message',
    method: 'post',
    data
  })
}

// 获取聊天消息历史记录
export const getChatHistory = () => {
  return request({
    url: '/chat/history',
    method: 'get'
  })
}

// 发送用户反馈
export const sendFeedback = (data) => {
  return request({
    url: '/chat/feedback',
    method: 'post',
    data
  })
}