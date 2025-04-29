<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <!-- <img src="../assets/logo.png" alt="Logo" /> -->
        <span>智简未来后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :router="true"
        :collapse="isCollapse"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>数据概览</template>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/resumes">
          <el-icon><Document /></el-icon>
          <template #title>简历管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/jobs">
          <el-icon><Briefcase /></el-icon>
          <template #title>职位管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/applications">
          <el-icon><List /></el-icon>
          <template #title>投递管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="link"
            class="collapse-btn"
            @click="toggleSidebar"
          >
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb>
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-dropdown">
              <el-avatar :size="32" :src="adminInfo.avatar" />
              <span class="username">{{ adminInfo.name }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">个人信息</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  DataLine,
  User,
  Document,
  Briefcase,
  List,
  Fold,
  Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态
const isCollapse = ref(false)
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 当前路由名称
const currentRoute = computed(() => {
  const routeMap: Record<string, string> = {
    '/admin/dashboard': '数据概览',
    '/admin/users': '用户管理',
    '/admin/resumes': '简历管理',
    '/admin/jobs': '职位管理',
    '/admin/applications': '投递管理'
  }
  return routeMap[route.path] || ''
})

// 管理员信息
const adminInfo = {
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  name: 'Admin'
}

// 处理个人信息
const handleProfile = () => {
  router.push('/admin/profile')
}

// 处理退出登录
const handleLogout = () => {
  // 清除所有认证相关的数据
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  
  .sidebar {
    background-color: #EEF5FE;
    height: 100%;
    transition: width 0.3s;
    overflow-x: hidden;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      padding: 0 20px;
      color: #1E3050;
      font-size: 20px;
      font-weight: 600;
      background-color: #FFFFFF;
      border-bottom: 1px solid #E6EBF5;
      
      img {
        width: 32px;
        height: 32px;
        margin-right: 12px;
      }
    }
    
    .sidebar-menu {
      border-right: none;
      background-color: transparent;
      margin-top: 10px;
      
      :deep(.el-menu-item) {
        height: 56px;
        line-height: 56px;
        margin: 8px 16px;
        border-radius: 8px;
        color: #1E3050;
        
        &.is-active {
          background-color: #409EFF;
          color: white;
          
          &::before {
            display: none;
          }
        }
        
        &:hover {
          background-color: #E6F0FF;
          color: #409EFF;
        }

        .el-icon {
          font-size: 18px;
          margin-right: 10px;
        }
      }
    }
  }
  
  .main-container {
    flex: 1;
    overflow: hidden;
    
    .header {
      background-color: white;
      border-bottom: 1px solid #dcdfe6;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .collapse-btn {
          padding: 0;
          font-size: 20px;
        }
      }
      
      .header-right {
        .user-dropdown {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .username {
            margin-left: 8px;
            font-size: 14px;
          }
        }
      }
    }
    
    .el-main {
      background-color: #f0f2f5;
      height: calc(100vh - 60px);
      overflow-y: auto;
      padding: 20px;
    }
  }
}
</style> 