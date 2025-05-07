import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

// 前台路由
const frontendRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/FrontendLayout.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/frontend/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'resume',
        name: 'Resume',
        component: () => import('@/views/frontend/Resume.vue'),
        meta: { title: '简历管理', requiresAuth: true }
      },
      {
        path: 'jobs',
        name: 'Jobs',
        component: () => import('@/views/frontend/Jobs.vue'),
        meta: { title: '职位推荐', requiresAuth: true }
      },
      {
        path: 'applications',
        name: 'Applications',
        component: () => import('@/views/frontend/Applications.vue'),
        meta: { title: '投递记录', requiresAuth: true }
      },
      {
        path: 'ai-chat',
        name: 'AiChat',
        component: () => import('@/views/frontend/AiChat.vue'),
        meta: { title: 'AI对话', requiresAuth: true }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/frontend/Settings.vue'),
        meta: { title: '账号设置', requiresAuth: true }
      },
      {
        path: 'resumeModel/:resumeid?',
        name: 'ResumeModel',
        component: () => import('@/views/frontend/resumeModel.vue'),
        meta: { title: '简历模板', requiresAuth: true }
      }
    ]
  }
]

// 后台路由
const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'resumes',
        name: 'ResumeManagement',
        component: () => import('@/views/admin/Resumes.vue'),
        meta: { title: '简历查看', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'jobs',
        name: 'JobManagement',
        component: () => import('@/views/admin/JobManagement.vue'),
        meta: { title: '职位管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: 'applications',
        name: 'ApplicationManagement',
        component: () => import('@/views/admin/Applications.vue'),
        meta: { title: '投递管理', requiresAuth: true, isAdmin: true }
      }
    ]
  }
]

// 通用路由
const commonRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/common/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/frontend/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...commonRoutes, ...frontendRoutes, ...adminRoutes]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log('Navigation guard triggered')
  console.log('From:', from.path)
  console.log('To:', to.path)
  console.log('Route meta:', to.meta)
  console.log('Route params:', to.params)
  console.log('Route query:', to.query)
  console.log('Auth required:', to.meta.requiresAuth)

  const userInfo = localStorage.getItem('userInfo')
  const token = localStorage.getItem('token')

  console.log('User info exists:', !!userInfo)
  console.log('Token exists:', !!token)
  console.log('User info:', userInfo)

  // 检查是否需要登录
  if (to.meta.requiresAuth && (!userInfo || !token)) {
    console.log('Auth required but not logged in, redirecting to login')
    next('/login')
    return
  }

  // 检查管理员权限
  if (to.meta.isAdmin) {
    const user = JSON.parse(userInfo || '{}')
    if (user.role !== 'admin') {
      console.log('Admin required but user is not admin, redirecting to home')
      next('/')
      return
    }
  }

  console.log('Navigation allowed')
  next()
})

export default router 