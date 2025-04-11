import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 对chat接口进行参数预检查
    if (config.url === '/chat' && config.method === 'post') {
      const { message, sessionId } = config.data
      if (!message || !sessionId) {
        return Promise.reject(new Error('消息内容和会话ID不能为空'))
      }
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code && res.code !== 200) {
      const errorMsg = res.message || '请求失败'
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      const errorMsg = error.response?.data?.message || error.message || '请求失败，请稍后重试'
      ElMessage.error(errorMsg)
    }
    return Promise.reject(error)
  }
)

export default service