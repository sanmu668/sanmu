import { RouteRecordRaw } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'

const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: {
          title: '数据概览',
          icon: 'Histogram'
        }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: {
          title: '用户管理',
          icon: 'User'
        }
      },
      {
        path: 'resumes',
        name: 'Resumes',
        component: () => import('@/views/admin/Resumes.vue'),
        meta: {
          title: '简历管理',
          icon: 'Document'
        }
      },
      {
        path: 'jobs',
        name: 'JobManagement',
        component: () => import('@/views/admin/JobManagement.vue'),
        meta: {
          title: '职位管理',
          icon: 'Briefcase'
        }
      },
      {
        path: 'applications',
        name: 'ApplicationManagement',
        component: () => import('@/views/admin/Applications.vue'),
        meta: {
          title: '投递管理',
          icon: 'List'
        }
      }
    ]
  }
]

export default adminRoutes 