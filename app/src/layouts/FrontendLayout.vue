<template>
  <el-container class="layout-container">
    <el-aside width="240px">
      <div class="sidebar">
        <div class="logo">
          <!-- <img src="/src/assets/logo.png" alt="Logo" class="logo-img" /> -->
          <h3>智简未来</h3>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          :collapse="isCollapse"
          :router="true"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item index="/resume" @click="handleMenuClick('/resume')">
            <el-icon><Document /></el-icon>
            <template #title>我的简历</template>
          </el-menu-item>
          <el-menu-item index="/resumeModel">
            <el-icon><Files /></el-icon>
            <template #title>简历模板</template>
          </el-menu-item>
          <el-menu-item index="/jobs">
            <el-icon><Briefcase /></el-icon>
            <template #title>职位推荐</template>
          </el-menu-item>
          <el-menu-item index="/applications">
            <el-icon><List /></el-icon>
            <template #title>投递记录</template>
          </el-menu-item>
          <el-menu-item index="/ai-chat">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>智能客服</template>
          </el-menu-item>
        </el-menu>
      </div>
    </el-aside>

    <el-container>
      <el-header height="70px">
        <div class="header-content">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-button class="notification-btn" circle>
              <el-icon><Bell /></el-icon>
            </el-button>
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-dropdown">
                <el-avatar :size="40" :src="userAvatar" />
                <span class="username">{{ userEmail }}</span>
                <el-icon class="el-icon--right"><CaretBottom /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  HomeFilled,
  Document,
  Briefcase,
  List,
  ChatDotRound,
  Bell,
  CaretBottom,
  User,
  SwitchButton,
  Files,
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const isCollapse = ref(false);

// Add route watcher for debugging
watch(
  () => route.path,
  (newPath) => {
    console.log('Route changed to:', newPath);
    console.log('Current route full object:', route);
  }
);

onMounted(() => {
  console.log('FrontendLayout mounted, initial route:', route.path);
});

const activeMenu = computed(() => {
  console.log('Computing active menu, current path:', route.path);
  return route.path;
});
const currentRoute = computed(() => {
  const routeMap: Record<string, string> = {
    "/": "首页",
    "/resume": "我的简历",
    "/resumeModel": "简历模板",
    "/jobs": "职位推荐",
    "/applications": "投递记录",
    "/ai-chat": "智能客服",
    "/settings": "个人信息",
  };
  return routeMap[route.path] || "";
});

const userEmail = computed(() => {
  const user = localStorage.getItem("user");
  return user ? JSON.parse(user).email : "";
});
const userAvatar = ref(
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
);

const handleCommand = (command: string) => {
  if (command === "logout") {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    router.push("/login");
  } else if (command === "profile") {
    router.push("/settings");
  }
};

const handleMenuClick = (path: string) => {
  console.log('Menu item clicked:', path);
  router.push(path);
};
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;

  .sidebar {
    height: 100%;
    background: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
    color: white;

    .logo {
      height: 70px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 20px;
      background: rgba(255, 255, 255, 0.1);

      .logo-img {
        width: 32px;
        height: 32px;
        margin-right: 10px;
      }

      h3 {
        color: white;
        margin: 0;
        font-size: 1.2rem;
        font-weight: 600;
      }
    }

    .el-menu {
      border-right: none;
      background: transparent;

      .el-menu-item {
        height: 50px;
        line-height: 50px;
        color: rgba(255, 255, 255, 0.7);
        margin: 8px 0;
        border-radius: 0 25px 25px 0;
        margin-right: 20px;

        .el-icon {
          color: rgba(255, 255, 255, 0.7);
        }

        &:hover,
        &.is-active {
          background: rgba(255, 255, 255, 0.1);
          color: white;

          .el-icon {
            color: white;
          }
        }
      }
    }
  }

  .el-header {
    background: white;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 0;

    .header-content {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;

      .breadcrumb {
        .el-breadcrumb {
          font-size: 14px;
        }
      }

      .header-right {
        display: flex;
        align-items: center;
        gap: 20px;

        .notification-btn {
          background: #f5f7fa;
          border: none;

          &:hover {
            background: #e4e7eb;
          }

          .el-icon {
            font-size: 18px;
            color: #606266;
          }
        }

        .user-dropdown {
          display: flex;
          align-items: center;
          cursor: pointer;
          padding: 5px 10px;
          border-radius: 25px;
          transition: all 0.3s ease;

          &:hover {
            background: #f5f7fa;
          }

          .username {
            margin: 0 10px;
            color: #606266;
            font-size: 14px;
          }
        }
      }
    }
  }

  .el-main {
    background-color: #f0f2f5;
    padding: 24px;
  }
}

// Transition animations
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 