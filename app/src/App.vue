<template>
  <el-container class="app-wrapper">
    <el-header height="60px" class="app-header">
      <div class="header-left">
        <img src="/logo.png" alt="logo" class="logo" />
        <h1 class="title">AI智能客服平台</h1>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ userStore.username || '未登录' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateTo('/profile')">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="app-aside">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          :router="true"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="/resume">
            <el-icon><Document /></el-icon>
            <span>简历管理</span>
          </el-menu-item>
          <el-menu-item index="/chat">
            <el-icon><ChatDotRound /></el-icon>
            <span>AI对话</span>
          </el-menu-item>
          <el-menu-item index="/jobs">
            <el-icon><Briefcase /></el-icon>
            <span>职位推荐</span>
          </el-menu-item>
          <el-menu-item index="/analysis">
            <el-icon><TrendCharts /></el-icon>
            <span>数据分析</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const navigateTo = (path) => {
  router.push(path)
}

const handleLogout = () => {
  router.go(0)
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  height: 100vh;
}

.app-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;

    .logo {
      height: 40px;
      margin-right: 10px;
    }

    .title {
      font-size: 20px;
      color: #303133;
      margin: 0;
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;

      .username {
        margin-left: 8px;
        color: #606266;
      }
    }
  }
}

.app-aside {
  background-color: #fff;
  border-right: 1px solid #dcdfe6;

  .el-menu-vertical {
    border-right: none;
  }
}

.app-main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>