// API配置
export const API_CONFIG = {
  // 优先使用环境变量中的配置,如果没有则使用默认值
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  TIMEOUT: 30000, // 请求超时时间
  TIMEOUT_MESSAGE: '请求超时，请检查网络连接或稍后重试'
}

// 其他全局配置
export const APP_CONFIG = {
  APP_NAME: 'AI Resume System',
  VERSION: '1.0.0'
}

// API endpoints
export const API_ENDPOINTS = {
  // 用户相关
  USER: {
    RESUMES: '/api/user/resumes',
    JOBS: '/api/user/jobs',
    APPLICATIONS: '/api/applications/user'
  },
  // 管理员相关
  ADMIN: {
    RESUMES: '/api/admin/resumes',
    APPLICATIONS: '/api/admin/applications'
  },
  // 应用相关
  APPLICATIONS: {
    SUBMIT: '/api/applications/sumit'
  }
} 