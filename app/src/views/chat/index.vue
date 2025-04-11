<template>
  <div class="chat-container">
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <span>AI智能对话</span>
          <el-tag type="success" v-if="userStore.subscriptionType === 'pro'">专业版</el-tag>
          <el-tag v-else>免费版</el-tag>
        </div>
      </template>
      
      <div class="chat-content" ref="chatContentRef">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
          <el-avatar :size="32" :icon="message.type === 'user' ? 'UserFilled' : 'Service'" />
          <div class="message-content">
            <p>{{ message.content }}</p>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keyup.enter="handleSend"
        />
        <el-button type="primary" @click="handleSend" :loading="loading">
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { sendMessage, sendChatMessage } from '@/api/chat'

const userStore = useUserStore()
const chatContentRef = ref(null)
const inputMessage = ref('')
const loading = ref(false)
const messages = ref([])

const scrollToBottom = async () => {
  await nextTick()
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
  }
}

import { debounce } from 'lodash-es'

let abortController = null

const handleSend = debounce(async () => {
  try {
    const message = inputMessage.value.trim()
    if (!message) {
      return
    }

    // 清空输入并显示用户消息
    inputMessage.value = ''
    const userMessage = {
      type: 'user',
      content: message
    }
    messages.value.push(userMessage)
    await scrollToBottom()

    // 创建新的AI消息
    const aiMessage = {
      type: 'ai',
      content: '正在思考中...'
    }
    messages.value.push(aiMessage)
    await scrollToBottom()

    // 发送请求
    loading.value = true
    const sessionId = localStorage.getItem('chatSessionId') || Date.now().toString()
    localStorage.setItem('chatSessionId', sessionId)

    const response = await sendChatMessage(message, sessionId)
    console.log('Chat Response:', response)
    
    if (!response) {
      throw new Error('服务器响应为空')
    }
    
    if (typeof response.content !== 'string') {
      console.error('Invalid response format:', response)
      throw new Error('服务器响应格式错误')
    }

    // 更新AI消息内容
    aiMessage.content = response.content
    await scrollToBottom()
  } catch (error) {
    console.error('发送消息失败:', error)
    if (messages.value.length > 0) {
      const lastMessage = messages.value[messages.value.length - 1]
      if (lastMessage.type === 'ai') {
        if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
          lastMessage.content = '抱歉，响应时间过长。请重试或换个简单的问题。'
        } else {
          lastMessage.content = '抱歉，发生了错误。请稍后重试。'
        }
      }
    }
    ElMessage.error(error?.message || '发送消息失败，请稍后重试')
  } finally {
    loading.value = false
  }
}, 300)

onMounted(() => {
  messages.value = [{
    type: 'ai',
    content: '你好，我是你的智能简历助手！📄🤖\n\n我可以帮你解析简历，提取你的核心优势，并结合大数据分析，提供个性化的职业规划建议。无论你是求职新手还是职场老手，我都能帮你优化简历，模拟面试，推荐适合你的职业方向，助你在求职路上更进一步！💡✨\n\n你可以随时向我提问，例如：\'如何优化我的简历？\' 或 \'我的简历适合哪些岗位？\' 让我们一起打造一份脱颖而出的简历吧！🎯🔥'
  }]
})
</script>

<style lang="scss" scoped>
.chat-container {
  height: calc(100vh - 140px);
  padding: 20px;

  .chat-card {
    height: 100%;
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding: 0;
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .chat-content {
    height: calc(100% - 120px);
    overflow-y: auto;
    padding: 20px;
  }
  
  .chat-input {
    position: sticky;
    bottom: 0;
    background: white;
    padding: 20px;
    border-top: 1px solid #dcdfe6;
  }
  
  :deep(.el-card__body) {
    height: 100%;
    overflow: hidden;
  }

    .message {
      display: flex;
      align-items: flex-start;
      margin-bottom: 20px;

      &.ai {
        flex-direction: row;

        .message-content {
          margin-left: 10px;
          background-color: #f4f4f5;
        }
      }

      &.user {
        flex-direction: row-reverse;

        .message-content {
          margin-right: 10px;
          background-color: #e6f6ff;
        }
      }

      .message-content {
        max-width: 80%;
        padding: 10px 15px;
        border-radius: 8px;

        p {
          margin: 0;
          white-space: pre-wrap;
          word-break: break-word;
        }
      }
    }

    .chat-input {
      padding: 20px;
      border-top: 1px solid #dcdfe6;

      .el-button {
        width: 100%;
        margin-top: 10px;
      }
    }
  }
</style>