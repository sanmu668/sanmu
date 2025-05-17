<template>
  <div class="ai-chat-container">
    <div class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
      <div class="sidebar-header">
        <el-button type="primary" class="new-chat-btn" @click="startNewChat">
          <el-icon><component :is="icons.plus" /></el-icon> 新对话
        </el-button>
        <el-icon 
          :class="['collapse-icon', { 'collapsed': isSidebarCollapsed }]"
          @click="toggleSidebar"
        >
          <component :is="icons.fold" />
        </el-icon>
      </div>
      
      <div class="sidebar-content">
        <div class="sidebar-section">
          <h3>历史对话</h3>
          <div class="chat-history">
            <div v-for="chat in chatHistory" 
                 :key="chat.id" 
                 class="chat-item"
                 :class="{ 'selected': selectedChatId === chat.id }"
                 @click="loadChat(chat)">
              <el-icon><component :is="icons.chatRound" /></el-icon>
              <span>{{ chat.title }}</span>
              <el-icon class="operation-icon" @click="handleOperationClick($event, chat)">
                <component :is="icons.more" />
              </el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-window" :class="{ 'sidebar-collapsed': isSidebarCollapsed }">
      <div class="chat-header">
        <h2>AI 智能助手</h2>
      </div>
      <div class="messages" ref="messagesContainer">
        <div v-for="(message, index) in messages" 
             :key="index" 
             :class="['message-wrapper', message.type]">
          <div class="avatar">
            <el-avatar :size="36">
              <el-icon>
                <component :is="message.type === 'user' ? icons.user : icons.service" />
              </el-icon>
            </el-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              {{ message.content }}
            </div>
            <div class="message-time" v-if="message.time">
              {{ message.time }}
            </div>
          </div>
          <div v-if="message.type === 'user'" class="file-upload-icon">
            <el-upload
              class="upload-link"
              :action="null"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
            >
              <el-icon><component :is="icons.paperclip" /></el-icon>
            </el-upload>
          </div>
        </div>
      </div>
      <div class="input-area">
        <div class="input-wrapper">
          <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            :maxlength="1000"
            show-word-limit
            resize="none"
            placeholder="请输入您的问题..."
            @keyup.enter.exact.prevent="handleSendMessage"
          />
          <div class="action-buttons">
            <el-upload
              class="upload-btn"
              :action="null"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
              :before-upload="handleBeforeUpload"
              multiple
              :accept="acceptFileTypes"
            >
              <el-button type="primary" plain>
                <el-icon><component :is="icons.upload" /></el-icon>
                上传文件
              </el-button>
            </el-upload>
            <el-button 
              type="primary" 
              :loading="loading"
              :disabled="!userInput.trim()"
              @click="handleSendMessage">
              发送
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作菜单 -->
    <teleport to="body">
      <div v-if="showOperationMenu" 
           class="operation-menu"
           :style="{
             left: `${operationMenuPosition.x}px`,
             top: `${operationMenuPosition.y}px`
           }"
      >
        <div class="operation-menu-items">
          <template v-if="currentItem">
            <div class="operation-item" @click="handleRename(currentItem)">
              <el-icon><component :is="icons.edit" /></el-icon>
              <span>重命名</span>
            </div>
            <div class="operation-item delete" @click="handleDelete(currentItem)">
              <el-icon><component :is="icons.delete" /></el-icon>
              <span>删除</span>
            </div>
          </template>
        </div>
      </div>
    </teleport>

    <!-- 重命名对话框 -->
    <el-dialog
      v-model="isEditingName"
      title="重命名"
      width="300px"
      :show-close="true"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-input
        v-model="editingName"
        placeholder="请输入新名称"
        @keyup.enter="saveRename"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="isEditingName = false">取消</el-button>
          <el-button type="primary" @click="saveRename">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { sendMessage as sendChatMessage, createNewSession as createNewChatSession, getChatHistory } from '@/api/chat'
import type { ChatMessage } from '@/api/chat'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 注册所有图标组件
const icons = {
  plus: ElementPlusIconsVue.Plus,
  fold: ElementPlusIconsVue.Fold,
  document: ElementPlusIconsVue.Document,
  upload: ElementPlusIconsVue.Upload,
  loading: ElementPlusIconsVue.Loading,
  warning: ElementPlusIconsVue.Warning,
  chatRound: ElementPlusIconsVue.ChatRound,
  user: ElementPlusIconsVue.User,
  service: ElementPlusIconsVue.Service,
  more: ElementPlusIconsVue.MoreFilled,
  edit: ElementPlusIconsVue.EditPen,
  delete: ElementPlusIconsVue.Delete,
  paperclip: ElementPlusIconsVue.Paperclip
}

interface SavedChat {
  id: string
  sessionId: string
  title: string
  lastMessage?: string
  timestamp: string
}

interface LocalMessage extends ChatMessage {
  content: string
  time: string
}

const userInput = ref('')
const loading = ref(false)
const messages = ref<LocalMessage[]>([])
const messagesContainer = ref<HTMLElement | null>(null)
const isSidebarCollapsed = ref(false)
const currentSessionId = ref<string>('')
const currentChatTitle = ref<string>('')
const userId = ref<number | null>(null)
const selectedChatId = ref<string | null>(null)
const showOperationMenu = ref(false)
const operationMenuPosition = ref({ x: 0, y: 0 })
const isEditingName = ref(false)
const editingName = ref('')
const editingItemType = ref<'chat' | null>(null)

// PDF预览相关
const showPdfPreview = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)

// 状态变量
const chatHistory = ref<SavedChat[]>([])
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/api/resumes/upload'
const isUploading = ref(false)

const selectedChat = computed(() => chatHistory.value.find((c: SavedChat) => c.id === selectedChatId.value))

const router = useRouter()

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const getUserInfo = () => {
  // 检查 token
  const token = localStorage.getItem('token')
  console.log('Token:', token ? '存在' : '不存在')
  
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return false
  }

  // 获取用户信息
  const userInfo = localStorage.getItem('user')
  console.log('User Info:', userInfo)
  
  if (userInfo) {
    try {
      const parsedUserInfo = JSON.parse(userInfo)
      userId.value = parsedUserInfo.id || parsedUserInfo.userId
      console.log('从用户信息中获取到的用户ID:', userId.value)
      return true
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }

  // 如果没有用户信息但有token，尝试从token中解析
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))

    const tokenData = JSON.parse(jsonPayload)
    userId.value = tokenData.id || tokenData.userId || tokenData.sub
    console.log('从Token中解析到的用户ID:', userId.value)
    return true
  } catch (error) {
    console.error('解析Token失败:', error)
  }
  
  ElMessage.error('获取用户信息失败，请重新登录')
  router.push('/login')
  return false
}

const formatAIResponse = (text: string): string => {
  if (!text) return ''
  return text
    .replace(/#{1,}/g, '') // 移除所有的#号
    .replace(/\d+\./g, '\n$&') // 数字编号前添加换行
    .replace(/[一二三四五六七八九十]、/g, '\n$&') // 中文数字编号前添加换行
    .replace(/\s*-\s*/g, '\n- ') // 破折号前添加换行
    .trim() // 移除首尾空格
}

const startNewChat = async () => {
  if (!getUserInfo()) return
  
  try {
    const response = await createNewChatSession()
    if (response) {
      currentSessionId.value = response.sessionId
      currentChatTitle.value = ''
      messages.value = [{
        ...response,
        content: formatAIResponse(response.answer || '你好！我是AI助手，很高兴为您服务。请问有什么我可以帮您的吗？'),
        type: 'ai',
        time: formatTimeFromTimestamp(response.timestamp)
      }]
    }
  } catch (error: any) {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('创建新会话失败，请稍后重试')
      console.error('Error creating new session:', error)
    }
  }
}

const loadChat = async (chat: SavedChat) => {
  if (!getUserInfo()) return
  
  try {
    selectedChatId.value = chat.id
    currentSessionId.value = chat.sessionId
    currentChatTitle.value = chat.title
    
    const response = await getChatHistory({
      userId: userId.value!,
      sessionId: chat.sessionId
    })
    
    // 按时间戳排序消息
    const sortedMessages = response.sort((a, b) => 
      new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
    )
    
    // 将每条记录转换为问题和回答两条消息
    const allMessages: LocalMessage[] = []
    
    sortedMessages.forEach(msg => {
      // 添加用户问题
      if (msg.question) {
        allMessages.push({
          ...msg,
          content: msg.question,
          type: 'user',
          time: formatTimeFromTimestamp(msg.timestamp)
        })
      }
      
      // 添加AI回答
      if (msg.answer) {
        allMessages.push({
          ...msg,
          content: msg.answer,
          type: 'ai',
          time: formatTimeFromTimestamp(msg.timestamp)
        })
      }
    })
    
    messages.value = allMessages
    await scrollToBottom()
  } catch (error: any) {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error('加载历史对话失败')
      console.error('Error loading chat history:', error)
    }
  }
}

// 格式化时间
const formatTime = () => {
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化时间戳
const formatTimeFromTimestamp = (timestamp: string) => {
  if (!timestamp) return ''
  
  try {
    const date = new Date(timestamp)
    if (isNaN(date.getTime())) return ''
    
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${hours}:${minutes}`
  } catch (error) {
    return ''
  }
}

// 滚动到最新消息
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 保存对话到历史记录
const saveChatToHistory = () => {
  if (!currentSessionId.value || messages.value.length === 0) return
  
  // 获取最后一组对话（用户问题和AI回答）
  const lastUserMessage = messages.value.filter(m => m.type === 'user').pop()
  const lastAiMessage = messages.value.filter(m => m.type === 'ai').pop()
  
  const chatTitle = currentChatTitle.value || 
    (lastUserMessage ? lastUserMessage.content.slice(0, 20) + '...' : `对话 ${formatTime()}`)
  
  const savedChat: SavedChat = {
    id: Date.now().toString(),
    sessionId: currentSessionId.value,
    title: chatTitle,
    lastMessage: lastAiMessage?.content || lastUserMessage?.content || '',
    timestamp: formatTime()
  }
  
  // 检查是否已存在相同sessionId的对话
  const existingIndex = chatHistory.value.findIndex(chat => chat.sessionId === currentSessionId.value)
  if (existingIndex !== -1) {
    chatHistory.value[existingIndex] = savedChat
  } else {
    chatHistory.value.unshift(savedChat)
  }
  
  // 保存到localStorage
  localStorage.setItem('chatHistory', JSON.stringify(chatHistory.value))
}

// 从localStorage加载历史对话
const loadChatHistory = () => {
  const savedHistory = localStorage.getItem('chatHistory')
  if (savedHistory) {
    chatHistory.value = JSON.parse(savedHistory)
  }
}

// 在发送消息后保存对话
const handleSendMessage = async () => {
  if (!userInput.value.trim() || !getUserInfo()) return
  
  if (!currentSessionId.value) {
    await startNewChat()
  }
  
  const timestamp = new Date().toISOString()
  
  // 添加用户消息
  const userMessage: LocalMessage = {
    id: Date.now(),
    userId: userId.value!,
    sessionId: currentSessionId.value,
    content: userInput.value,
    question: userInput.value,
    type: 'user',
    time: formatTime(),
    timestamp: timestamp
  }
  messages.value.push(userMessage)
  
  const userQuestion = userInput.value
  userInput.value = ''
  await scrollToBottom()
  
  loading.value = true
  try {
    const response = await sendChatMessage({
      userId: userId.value!,
      sessionId: currentSessionId.value,
      question: userQuestion
    })
    
    if (response) {
      const aiMessage: LocalMessage = {
        ...response,
        content: formatAIResponse(response.answer || ''),
        type: 'ai',
        time: formatTimeFromTimestamp(response.timestamp)
      }
      messages.value.push(aiMessage)
      saveChatToHistory()
    }
  } catch (error: any) {
    console.error('Error:', error)
    handleSendError(error)
    
    if (error.response?.status === 401) {
      router.push('/login')
    }
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const handleOperationClick = (event: MouseEvent, chat: SavedChat) => {
  event.stopPropagation()
  showOperationMenu.value = true
  const rect = (event.target as HTMLElement).getBoundingClientRect()
  operationMenuPosition.value = {
    x: rect.right - 100, // 向左偏移菜单宽度
    y: rect.top
  }
  selectedChatId.value = chat.id
  editingItemType.value = 'chat'
}

const handleRename = async (item: SavedChat) => {
  editingName.value = item.title
  isEditingName.value = true
  editingItemType.value = 'chat'
  showOperationMenu.value = false
}

const saveRename = () => {
  if (editingItemType.value === 'chat' && selectedChat.value) {
    const index = chatHistory.value.findIndex(c => c.id === selectedChatId.value)
    if (index !== -1) {
      chatHistory.value[index].title = editingName.value
      localStorage.setItem('chatHistory', JSON.stringify(chatHistory.value))
    }
  }
  isEditingName.value = false
  editingName.value = ''
  editingItemType.value = null
}

const handleDelete = async (item: SavedChat) => {
  const index = chatHistory.value.findIndex(c => c.id === item.id)
  if (index !== -1) {
    chatHistory.value.splice(index, 1)
    localStorage.setItem('chatHistory', JSON.stringify(chatHistory.value))
    if (selectedChatId.value === item.id) {
      selectedChatId.value = null
      await startNewChat()
    }
  }
  showOperationMenu.value = false
}

// Close operation menu when clicking outside
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.operation-menu') && !target.closest('.operation-icon')) {
    showOperationMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 在组件挂载时检查用户登录状态并输出调试信息
onMounted(async () => {
  console.log('组件挂载，开始检查登录状态...')
  console.log('LocalStorage内容:', {
    token: localStorage.getItem('token'),
    user: localStorage.getItem('user')
  })
  
  if (getUserInfo()) {
    console.log('用户已登录，userId:', userId.value)
    loadChatHistory()
    await startNewChat()
  } else {
    console.log('用户未登录或登录状态无效')
  }
})

// 添加计算属性
const currentItem = computed(() => {
  if (editingItemType.value === 'chat' && selectedChat.value) {
    return selectedChat.value
  }
  return null
})

const handlePdfRendered: (numPages: number) => void = (numPages) => {
  totalPages.value = numPages
}

// 添加文件类型限制
const acceptFileTypes = '.jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx,.txt'

// 添加文件上传相关方法
const handleBeforeUpload = (file: File) => {
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  return true
}

const handleFileChange = (file: any) => {
  if (handleBeforeUpload(file.raw)) {
    handleSendMessageWithFile(file.raw)
  }
}

const handleSendMessageWithFile = async (file: File) => {
  if (!getUserInfo()) return
  
  if (!currentSessionId.value) {
    await startNewChat()
  }
  
  const timestamp = new Date().toISOString()
  
  // 添加用户消息
  const userMessage: LocalMessage = {
    id: Date.now(),
    userId: userId.value!,
    sessionId: currentSessionId.value,
    content: `上传文件：${file.name}`,
    question: `上传文件：${file.name}`,
    type: 'user',
    time: formatTime(),
    timestamp: timestamp
  }
  messages.value.push(userMessage)
  await scrollToBottom()
  
  loading.value = true
  try {
    const response = await sendChatMessage({
      userId: userId.value!,
      sessionId: currentSessionId.value,
      file: file
    })
    
    if (response) {
      const aiMessage: LocalMessage = {
        ...response,
        content: response.answer || '',
        type: 'ai',
        time: formatTimeFromTimestamp(response.timestamp)
      }
      messages.value.push(aiMessage)
      saveChatToHistory()
    }
  } catch (error: any) {
    console.error('Error:', error)
    handleSendError(error)
    
    if (error.response?.status === 401) {
      router.push('/login')
    }
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const handleSendError = (error: any) => {
  const errorTimestamp = new Date().toISOString()
  let errorContent = '抱歉，服务出现了一些问题，请稍后再试。'

  if (error.code === 'ECONNABORTED') {
    errorContent = error.message || '请求响应时间过长，请稍后重试。'
  } else if (error.response) {
    switch (error.response.status) {
      case 401:
        errorContent = '会话已过期，请刷新页面重新开始对话。'
        break
      case 429:
        errorContent = '请求过于频繁，请稍后再试。'
        break
      case 500:
        errorContent = '服务器内部错误，请稍后重试。'
        break
      case 502:
        errorContent = '服务暂时不可用，请稍后重试。'
        break
      case 503:
        errorContent = '服务器正在维护，请稍后重试。'
        break
      case 504:
        errorContent = '服务器响应超时，请稍后重试。'
        break
    }
  } else if (error.request) {
    errorContent = '无法连接到服务器，请检查网络连接。'
  }

  const errorMessage: LocalMessage = {
    id: Date.now(),
    userId: userId.value!,
    sessionId: currentSessionId.value,
    content: errorContent,
    type: 'ai',
    time: formatTime(),
    timestamp: errorTimestamp
  }

  messages.value.push(errorMessage)
  ElMessage.error({
    message: errorContent,
    duration: 5000,
    showClose: true
  })

  // 如果是认证相关错误，延迟跳转到登录页
  if (error.response?.status === 401) {
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  }
}

const handleFileUploadError = () => {
  ElMessage.error('文件上传失败，请重试')
}
</script>

<style lang="scss" scoped>
.ai-chat-container {
  height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
  display: flex;
  gap: 20px;
  
  .sidebar {
    width: 260px;
    background: linear-gradient(to bottom, #ffffff, #f8f9fa);
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    &.collapsed {
      width: 60px;
      .sidebar-content, .new-chat-btn span {
        display: none;
      }
    }
    
    .sidebar-header {
      padding: 16px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: #ffffff;
      
      .new-chat-btn {
        background: #409EFF;
        border: none;
        border-radius: 12px;
        padding: 10px 16px;
        transition: all 0.3s ease;
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        font-weight: 500;
        
        &:hover {
          background: #66b1ff;
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
        }
        
        &:active {
          transform: translateY(0);
        }
      }
    }
    
    .sidebar-content {
      flex: 1;
      overflow-y: auto;
      padding: 12px;

      .chat-item {
        padding: 12px;
        border-radius: 12px;
        margin-bottom: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: 8px;
        color: #606266;
        
        &:hover {
          background: rgba(64, 158, 255, 0.1);
          color: #409EFF;
        }
        
        &.selected {
          background: rgba(64, 158, 255, 0.15);
          color: #409EFF;
          font-weight: 500;
        }

        .el-icon {
          font-size: 18px;
        }
      }
    }
  }

  .chat-window {
    flex: 1;
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
    padding: 0 20px;
    overflow: hidden;
    position: relative;
    
    .chat-header {
      padding: 20px 0;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: #ffffff;
      position: sticky;
      top: 0;
      z-index: 1;
      
      h2 {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        display: flex;
        align-items: center;
        gap: 8px;
        
        &::before {
          content: '';
          display: block;
          width: 4px;
          height: 20px;
          background: #409EFF;
          border-radius: 2px;
        }
      }
    }

    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 20px 0;
      padding-bottom: 140px;
      scroll-behavior: smooth;

      .message-wrapper {
        display: flex;
        margin-bottom: 24px;
        
        .avatar {
          .el-avatar {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            border: 2px solid #ffffff;
            transition: all 0.3s ease;
            
            &:hover {
              transform: scale(1.05);
            }
          }
        }
        
        .message-content {
          margin: 0 12px;
          max-width: 80%;
          
          .message-bubble {
            padding: 12px 16px;
            border-radius: 12px;
            position: relative;
            line-height: 1.6;
            font-size: 14px;
            transition: all 0.3s ease;
            white-space: pre-line;
            
            &:hover {
              transform: translateY(-1px);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            }
          }
          
          .message-time {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
            opacity: 0.8;
          }
        }
        
        &.user {
          flex-direction: row-reverse;
          
          .message-content {
            .message-bubble {
              background: linear-gradient(135deg, #409EFF, #66b1ff);
              color: white;
              border-radius: 12px 12px 0 12px;
            }
            
            .message-time {
              text-align: right;
            }
          }
          
          .avatar .el-avatar {
            background: #409EFF;
          }
        }
        
        &.ai {
          .message-content {
            .message-bubble {
              background: #f4f6f8;
              color: #303133;
              border-radius: 12px 12px 12px 0;
              
              p {
                margin: 8px 0;
                &:first-child {
                  margin-top: 0;
                }
                &:last-child {
                  margin-bottom: 0;
                }
              }
              
              ul, ol {
                margin: 8px 0;
                padding-left: 20px;
              }
              
              li {
                margin: 4px 0;
              }
            }
          }
          
          .avatar .el-avatar {
            background: #67C23A;
          }
        }
      }
    }

    .input-area {
      position: fixed;
      bottom: 0;
      left: 280px;
      right: 20px;
      padding: 20px;
      background: rgba(255, 255, 255, 0.9);
      backdrop-filter: blur(10px);
      border-top: 1px solid rgba(0, 0, 0, 0.06);
      transition: all 0.3s ease;
      
      .input-wrapper {
        max-width: 1200px;
        margin: 0 auto;
        display: flex;
        flex-direction: column;
        gap: 12px;
        
        .el-textarea {
          .el-textarea__inner {
            border-radius: 12px;
            border: 1px solid rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            padding: 12px 16px;
            min-height: 60px;
            
            &:focus {
              border-color: #409EFF;
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
            }
          }
        }
        
        .action-buttons {
          display: flex;
          justify-content: flex-end;
          gap: 12px;
          
          .el-button {
            border-radius: 12px;
            padding: 10px 24px;
            font-weight: 500;
            transition: all 0.3s ease;
            
            &:hover {
              transform: translateY(-1px);
              box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
            }
            
            &:active {
              transform: translateY(0);
            }
          }
          
          .upload-btn {
            .el-button {
              background: #f4f6f8;
              border: none;
              color: #606266;
              
              &:hover {
                background: #e8eaec;
              }
            }
          }
        }
      }
    }

    &.sidebar-collapsed {
      .input-area {
        left: 80px; // 60px (collapsed sidebar) + 20px (gap)
      }
    }
  }
}

// 自定义滚动条样式
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
  
  &:hover {
    background: #909399;
  }
}

// 添加响应式设计
@media screen and (max-width: 768px) {
  .ai-chat-container {
    padding: 10px;
    
    .sidebar {
      width: 200px;
      
      &.collapsed {
        width: 0;
      }
    }
    
    .chat-window {
      .input-area {
        left: 200px;
        padding: 10px;
        
        .input-wrapper {
          .action-buttons {
            flex-wrap: wrap;
          }
        }
      }
    }
  }
}

.messages {
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c0c4cc;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f5f7fa;
  }
}

.el-icon {
  &.collapse-icon {
    font-size: 20px;
    cursor: pointer;
    transition: all 0.3s;
    color: #909399;
    
    &:hover {
      color: #409EFF;
    }
    
    &.collapsed {
      transform: rotate(180deg);
    }
  }
}

.message-wrapper {
  position: relative;
  
  .file-upload-icon {
    position: absolute;
    right: -30px;
    top: 50%;
    transform: translateY(-50%);
    opacity: 0;
    transition: opacity 0.3s;
    
    .upload-link {
      cursor: pointer;
      color: #909399;
      
      &:hover {
        color: #409EFF;
      }
      
      .el-icon {
        font-size: 16px;
      }
    }
  }
  
  &:hover {
    .file-upload-icon {
      opacity: 1;
    }
  }
}

.operation-menu {
  position: fixed;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-width: 100px;
  padding: 4px 0;
  margin: 0;
  z-index: 2000;

  .operation-menu-items {
    .operation-item {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      cursor: pointer;
      white-space: nowrap;
      font-size: 13px;
      transition: all 0.3s;
      
      &:hover {
        background: #f5f7fa;
      }
      
      .el-icon {
        font-size: 14px;
      }
      
      &.delete {
        color: #f56c6c;
        
        &:hover {
          background: #fef0f0;
        }
      }
    }
  }
}

.pdf-container {
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  gap: 20px;
  
  :deep(iframe) {
    width: 100%;
    height: 100%;
    border: none;
  }
  
  .pdf-controls {
    display: flex;
    justify-content: center;
    padding: 16px;
    background: white;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  
  h3 {
    margin: 0;
    font-size: 14px;
    color: #909399;
  }
}

.resume-list {
  .resume-item {
    &.analyzing {
      background-color: rgba(64, 158, 255, 0.1);
      
      .status-icon {
        animation: rotating 2s linear infinite;
      }
    }
    
    &.error {
      background-color: rgba(245, 108, 108, 0.1);
      
      .status-icon {
        color: #f56c6c;
      }
    }
    
    .resume-info {
      flex: 1;
      min-width: 0;
      
      .resume-name {
        display: block;
        font-size: 14px;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      
      .resume-meta {
        display: block;
        font-size: 12px;
        color: #909399;
      }
    }
    
    .status-icon {
      margin-right: 8px;
      font-size: 16px;
      color: #409EFF;
    }
  }
  
  .empty-tip {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px;
    color: #909399;
    
    .el-icon {
      font-size: 32px;
      margin-bottom: 8px;
    }
    
    p {
      margin: 0;
      font-size: 14px;
    }
  }
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 