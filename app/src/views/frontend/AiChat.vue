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
          <div class="section-header">
            <h3>简历分析</h3>
            <el-upload
              class="upload-btn"
              :action="uploadUrl"
              :before-upload="handleBeforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :show-file-list="false"
              accept=".pdf"
            >
              <el-button type="primary" link>
                <el-icon><component :is="icons.upload" /></el-icon>
                上传简历
              </el-button>
            </el-upload>
          </div>
          <div class="resume-list">
            <div v-for="resume in resumes" 
                 :key="resume.id" 
                 class="resume-item"
                 :class="{ 
                   'selected': selectedResumeId === resume.id,
                   'analyzing': resume.status === 'analyzing',
                   'error': resume.status === 'error'
                 }"
                 @click="selectResume(resume, $event)"
                 draggable="true"
                 @dragstart="handleDragStart($event, resume)">
              <el-icon><component :is="icons.document" /></el-icon>
              <div class="resume-info">
                <span class="resume-name">{{ resume.name }}</span>
                <span class="resume-meta">
                  {{ formatFileSize(resume.fileSize) }} · {{ formatTime(resume.uploadTime) }}
                </span>
              </div>
              <el-icon 
                v-if="resume.status === 'analyzing'"
                class="status-icon loading">
                <component :is="icons.loading" />
              </el-icon>
              <el-icon 
                v-else-if="resume.status === 'error'"
                class="status-icon error">
                <component :is="icons.warning" />
              </el-icon>
              <el-icon class="operation-icon" @click.stop="handleResumeOperationClick($event, resume)">
                <component :is="icons.more" />
              </el-icon>
            </div>
            <div v-if="resumes.length === 0" class="empty-tip">
              <el-icon><component :is="icons.document" /></el-icon>
              <p>暂无简历，请上传PDF格式的简历文件</p>
            </div>
          </div>
        </div>
        
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
            <div class="message-time">
              {{ message.time }}
            </div>
          </div>
        </div>
      </div>
      <div class="input-area">
        <el-input
          v-model="userInput"
          type="textarea"
          :rows="3"
          :maxlength="1000"
          show-word-limit
          resize="none"
          placeholder="请输入您的问题..."
          @keyup.enter.exact.prevent="handleSendMessage"
          @drop.prevent="handleDrop"
          @dragover.prevent="handleDragOver"
          @dragenter="handleDragEnter"
          @dragleave="handleDragLeave"
          :class="{ 'drag-over': isDragOver }"
        />
        <el-button 
          type="primary" 
          :loading="loading"
          :disabled="!userInput.trim()"
          @click="handleSendMessage">
          发送
        </el-button>
      </div>
    </div>

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

    <!-- PDF预览对话框 -->
    <el-dialog
      v-model="showPdfPreview"
      :title="selectedResume?.name"
      width="80%"
      :fullscreen="true"
      destroy-on-close
    >
      <div class="pdf-container">
        <vue-pdf-embed
          v-if="selectedResume?.url"
          :source="selectedResume.url"
          :page="currentPage"
          @rendered="handlePdfRendered"
        />
        <div class="pdf-controls">
          <el-button-group>
            <el-button @click="currentPage--" :disabled="currentPage <= 1">
              上一页
            </el-button>
            <el-button>{{ currentPage }} / {{ totalPages }}</el-button>
            <el-button @click="currentPage++" :disabled="currentPage >= totalPages">
              下一页
            </el-button>
          </el-button-group>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { defineComponent } from 'vue'
import { sendMessage as sendChatMessage, createNewSession as createNewChatSession, getChatHistory } from '@/api/chat'
import type { ChatMessage, ChatSession } from '@/api/chat'
import { ElMessage } from 'element-plus'
import VuePdfEmbed from 'vue-pdf-embed'
import type { Resume } from '@/api/resume'
import { getResumeList, uploadResume, deleteResume, getResumeAnalysis } from '@/api/resume'

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
  delete: ElementPlusIconsVue.Delete
}

interface SavedChat extends ChatSession {
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
const userId = ref(1) // TODO: Get from user authentication
const selectedResumeId = ref<number | null>(null)
const selectedChatId = ref<string | null>(null)
const showOperationMenu = ref(false)
const operationMenuPosition = ref({ x: 0, y: 0 })
const isEditingName = ref(false)
const editingName = ref('')
const editingItemType = ref<'resume' | 'chat' | null>(null)

// PDF预览相关
const showPdfPreview = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)

// 状态变量
const resumes = ref<Resume[]>([])
const chatHistory = ref<SavedChat[]>([])
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/api/resumes/upload'
const isUploading = ref(false)

const selectedChat = computed(() => chatHistory.value.find((c: SavedChat) => c.id === selectedChatId.value))
const selectedResume = computed(() => resumes.value.find(r => r.id === selectedResumeId.value))

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const startNewChat = async () => {
  try {
    const response = await createNewChatSession()
    if (response) {
      currentSessionId.value = response.sessionId
      currentChatTitle.value = ''
      messages.value = [{
        ...response,
        content: response.answer || '',
        type: 'ai',
        time: formatTimeFromTimestamp(response.timestamp)
      }]
    }
  } catch (error) {
    ElMessage.error('创建新会话失败，请稍后重试')
    console.error('Error creating new session:', error)
  }
}

const selectResume = (resume: Resume, event?: MouseEvent) => {
  if (event?.detail === 2) { // 双击
    showPdfPreview.value = true
    currentPage.value = 1
    return
  }
  
  selectedResumeId.value = resume.id
  selectedChatId.value = null
  
  const timestamp = new Date().toISOString()
  messages.value = [{
    id: Date.now(),
    userId: userId.value,
    sessionId: currentSessionId.value || '',
    content: `我将为您分析简历：${resume.name}，请稍等...`,
    type: 'ai',
    time: formatTime(),
    timestamp: timestamp
  }]
}

const loadChat = async (chat: SavedChat) => {
  try {
    selectedChatId.value = chat.id
    selectedResumeId.value = null
    currentSessionId.value = chat.sessionId
    currentChatTitle.value = chat.title
    
    const response = await getChatHistory({
      userId: userId.value,
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
  } catch (error) {
    ElMessage.error('加载历史对话失败')
    console.error('Error loading chat history:', error)
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
  const date = new Date(timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
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
  if (!userInput.value.trim()) return
  if (!currentSessionId.value) {
    await startNewChat()
  }
  
  const timestamp = new Date().toISOString()
  
  // 添加用户消息
  const userMessage: LocalMessage = {
    id: Date.now(),
    userId: userId.value,
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
  let retryCount = 0
  const maxRetries = 2

  const tryRequest = async (): Promise<void> => {
    try {
      const response = await sendChatMessage({
        userId: userId.value,
        sessionId: currentSessionId.value,
        question: userQuestion
      })
      
      if (response) {
        const aiMessage: LocalMessage = {
          ...response,
          content: response.answer || '',
          type: 'ai',
          time: formatTimeFromTimestamp(response.timestamp)
        }
        messages.value.push(aiMessage)
        
        // 保存对话到历史记录
        saveChatToHistory()
      }
    } catch (error: any) {
      console.error('Error:', error)
      
      // 处理超时错误
      if (error.code === 'ECONNABORTED' && retryCount < maxRetries) {
        retryCount++
        ElMessage.warning(`请求超时，正在进行第${retryCount}次重试...`)
        return tryRequest() // 递归重试
      }

      const errorTimestamp = new Date().toISOString()
      const errorMessage: LocalMessage = {
        id: Date.now(),
        userId: userId.value,
        sessionId: currentSessionId.value,
        content: '抱歉，服务出现了一些问题，请稍后再试。',
        type: 'ai',
        time: formatTime(),
        timestamp: errorTimestamp
      }

      if (error.code === 'ECONNABORTED') {
        errorMessage.content = '请求响应时间过长，请稍后重试。'
      } else if (error.response) {
        switch (error.response.status) {
          case 401:
            errorMessage.content = '会话已过期，请刷新页面重新开始对话。'
            break
          case 429:
            errorMessage.content = '请求过于频繁，请稍后再试。'
            break
          case 500:
            errorMessage.content = '服务器内部错误，请稍后重试。'
            break
        }
      }

      messages.value.push(errorMessage)
      ElMessage.error(errorMessage.content)
    }
  }

  try {
    await tryRequest()
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const handleResumeOperationClick = (event: MouseEvent, resume: Resume) => {
  event.stopPropagation()
  showOperationMenu.value = true
  const rect = (event.target as HTMLElement).getBoundingClientRect()
  operationMenuPosition.value = {
    x: rect.right - 100, // 向左偏移菜单宽度
    y: rect.top
  }
  selectedResumeId.value = resume.id
  selectedChatId.value = null
  editingItemType.value = 'resume'
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
  selectedResumeId.value = null
  editingItemType.value = 'chat'
}

const handleRename = async (item: Resume | SavedChat) => {
  editingName.value = 'sessionId' in item ? item.title : item.name
  isEditingName.value = true
  editingItemType.value = 'sessionId' in item ? 'chat' : 'resume'
  showOperationMenu.value = false
}

const saveRename = () => {
  if (editingItemType.value === 'resume' && selectedResume.value) {
    const index = resumes.value.findIndex(r => r.id === selectedResumeId.value)
    if (index !== -1) {
      resumes.value[index].name = editingName.value
      localStorage.setItem('savedResumes', JSON.stringify(resumes.value))
    }
  } else if (editingItemType.value === 'chat' && selectedChat.value) {
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

const handleDelete = async (item: Resume | SavedChat) => {
  if ('id' in item) { // SavedChat
    const index = chatHistory.value.findIndex(c => c.id === item.id)
    if (index !== -1) {
      chatHistory.value.splice(index, 1)
      localStorage.setItem('chatHistory', JSON.stringify(chatHistory.value))
      if (selectedChatId.value === item.id) {
        selectedChatId.value = null
        await startNewChat()
      }
    }
  } else { // Resume
    const index = resumes.value.findIndex(r => r.id === item.id)
    if (index !== -1) {
      resumes.value.splice(index, 1)
      localStorage.setItem('savedResumes', JSON.stringify(resumes.value))
      if (selectedResumeId.value === item.id) {
        selectedResumeId.value = null
      }
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

// 在组件挂载时加载历史对话
onMounted(async () => {
  loadChatHistory()
  await startNewChat()
})

// 添加计算属性
const currentItem = computed(() => {
  if (editingItemType.value === 'chat' && selectedChat.value) {
    return selectedChat.value
  }
  if (editingItemType.value === 'resume' && selectedResume.value) {
    return selectedResume.value
  }
  return null
})

const handlePdfRendered: (numPages: number) => void = (numPages) => {
  totalPages.value = numPages
}

// 添加拖拽相关功能
const handleDragStart = (event: DragEvent, resume: Resume) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('text/plain', JSON.stringify(resume))
    event.dataTransfer.effectAllowed = 'copy'
  }
}

// 拖拽状态
const isDragOver = ref(false)

const handleDragEnter = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
}

// 修改 handleDrop 函数
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
  const data = event.dataTransfer?.getData('text/plain')
  if (data) {
    try {
      const resume = JSON.parse(data) as Resume
      userInput.value = `请分析这份简历：${resume.name}`
    } catch (error) {
      console.error('Invalid resume data:', error)
    }
  }
}

// 修改 handleDragOver 函数
const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'copy'
  }
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

// 格式化上传时间
const formatUploadTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 如果是今天的文件，只显示时间
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  
  // 如果是最近7天的文件，显示周几
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return days[date.getDay()]
  }
  
  // 其他情况显示完整日期
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

// 上传相关方法
const handleBeforeUpload = (file: File) => {
  if (file.type !== 'application/pdf') {
    ElMessage.error('只能上传PDF格式的文件')
    return false
  }
  if (file.size / 1024 / 1024 > 10) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  isUploading.value = true
  return true
}

const handleUploadSuccess = async (response: any) => {
  isUploading.value = false
  ElMessage.success('上传成功')
  await loadResumeList()
}

const handleUploadError = () => {
  isUploading.value = false
  ElMessage.error('上传失败，请重试')
}

// 加载简历列表
const loadResumeList = async () => {
  try {
    const response = await getResumeList()
    resumes.value = response.data
  } catch (error) {
    console.error('Error loading resumes:', error)
    ElMessage.error('加载简历列表失败')
  }
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
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    
    &.collapsed {
      width: 60px;
      .sidebar-content, .new-chat-btn span {
        display: none;
      }
    }
    
    .sidebar-header {
      padding: 16px;
      border-bottom: 1px solid #e4e7ed;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .new-chat-btn {
        flex: 1;
      }
      
      .collapse-icon {
        margin-left: 8px;
        cursor: pointer;
        transition: transform 0.3s;
        
        &.collapsed {
          transform: rotate(180deg);
        }
      }
    }
    
    .sidebar-content {
      flex: 1;
      overflow-y: auto;
      
      .sidebar-section {
        padding: 16px;
        
        h3 {
          margin: 0 0 12px;
          font-size: 14px;
          color: #909399;
        }
      }
      
      .resume-item, .chat-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 12px;
        margin-bottom: 8px;
        border-radius: 4px;
        cursor: pointer;
        position: relative;
        
        &:hover {
          background: #f5f7fa;
          
          .operation-icon {
            display: block;
          }
        }
        
        &.selected {
          background: #f0f2f5;
        }
        
        .el-icon {
          font-size: 16px;
          color: #909399;
        }
        
        span {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .operation-icon {
          display: none;
          position: absolute;
          right: 8px;
          color: #909399;
          cursor: pointer;
          padding: 2px;
          border-radius: 3px;
          
          &:hover {
            color: #409EFF;
            background-color: rgba(64, 158, 255, 0.1);
          }
        }
      }
    }
  }

  .chat-window {
    flex: 1;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    &.sidebar-collapsed {
      margin-left: -180px;
    }

    .chat-header {
      padding: 16px 20px;
      border-bottom: 1px solid #e4e7ed;
      
      h2 {
        margin: 0;
        color: #303133;
        font-size: 18px;
      }
    }
    
    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 20px;
      padding-bottom: 60px;
      
      .message-wrapper {
        display: flex;
        margin-bottom: 20px;
        
        .avatar {
          .el-avatar {
            .el-icon {
              font-size: 20px;
              color: white;
            }
          }
        }
        
        .message-content {
          flex: 1;
          
          .message-bubble {
            padding: 12px 16px;
            word-break: break-word;
            white-space: pre-wrap;
          }
          
          .message-time {
            margin-top: 4px;
            font-size: 12px;
            color: #909399;
          }
        }
        
        &.user {
          flex-direction: row-reverse;
          
          .message-content {
            margin-right: 12px;
            margin-left: 60px;
            
            .message-bubble {
              background: #409EFF;
              color: white;
              border-radius: 8px 2px 8px 8px;
            }
            
            .message-time {
              text-align: right;
            }
          }
          
          .avatar .el-avatar {
            background: #67C23A;
          }
        }
        
        &.ai {
          .message-content {
            margin-left: 12px;
            margin-right: 60px;
            
            .message-bubble {
              background: #f4f4f5;
              color: #303133;
              border-radius: 2px 8px 8px 8px;
            }
          }
          
          .avatar .el-avatar {
            background: #409EFF;
          }
        }
      }
    }
    
    .input-area {
      padding: 12px 20px;
      margin-top: -60px;
      border-top: 1px solid #e4e7ed;
      display: flex;
      gap: 12px;
      background: white;
      position: relative;
      z-index: 1;
      box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
      
      .el-textarea {
        flex: 1;
        
        :deep(.el-textarea__inner) {
          transition: all 0.3s;
          
          &.drag-over {
            border-color: #409EFF;
            border-style: dashed;
            background-color: rgba(64, 158, 255, 0.1);
          }
        }
      }
      
      .el-button {
        align-self: flex-end;
        min-width: 80px;
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
  &.user {
    .avatar .el-avatar {
      background: #67C23A;
    }
  }
  
  &.ai {
    .avatar .el-avatar {
      background: #409EFF;
    }
  }
}

.avatar {
  .el-avatar {
    background: #409EFF;
    
    .el-icon {
      font-size: 20px;
      color: white;
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

.resume-item {
  cursor: grab;
  
  &:active {
    cursor: grabbing;
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