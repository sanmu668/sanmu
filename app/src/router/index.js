import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard',

  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/index.vue'),

  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/index.vue'),

  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('@/views/resume/index.vue'),

  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/views/chat/index.vue'),

  },
  {
    path: '/jobs',
    name: 'Jobs',
    component: () => import('@/views/jobs/index.vue'),

  },
  {
    path: '/analysis',
    name: 'Analysis',
    component: () => import('@/views/analysis/index.vue'),

  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes
})



export default router