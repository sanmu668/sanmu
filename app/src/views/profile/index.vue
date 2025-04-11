<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" size="small" @click="handleEdit">编辑</el-button>
        </div>
      </template>
      <div class="profile-content">
        <el-avatar :size="64" icon="UserFilled" />
        <div class="info-list">
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userStore.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">订阅类型：</span>
            <span class="value">{{ userStore.subscriptionType === 'free' ? '免费版' : '专业版' }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="subscription-card">
      <template #header>
        <div class="card-header">
          <span>订阅信息</span>
        </div>
      </template>
      <div class="subscription-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="当前套餐">
            {{ userStore.subscriptionType === 'free' ? '免费版' : '专业版' }}
          </el-descriptions-item>
          <el-descriptions-item label="套餐权益">
            <ul class="benefits-list">
              <li v-if="userStore.subscriptionType === 'free'">
                <el-icon><Check /></el-icon>基础AI对话功能
              </li>
              <li v-if="userStore.subscriptionType === 'free'">
                <el-icon><Check /></el-icon>每月10次简历分析
              </li>
              <li v-if="userStore.subscriptionType === 'pro'">
                <el-icon><Check /></el-icon>无限AI对话功能
              </li>
              <li v-if="userStore.subscriptionType === 'pro'">
                <el-icon><Check /></el-icon>无限简历分析
              </li>
              <li v-if="userStore.subscriptionType === 'pro'">
                <el-icon><Check /></el-icon>高级数据分析
              </li>
            </ul>
          </el-descriptions-item>
        </el-descriptions>

        <div class="upgrade-section" v-if="userStore.subscriptionType === 'free'">
          <el-button type="primary" @click="handleUpgrade">升级到专业版</el-button>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="编辑个人信息"
      width="30%"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const dialogVisible = ref(false)
const form = ref({
  username: ''
})

const handleEdit = () => {
  form.value.username = userStore.username
  dialogVisible.value = true
}

const handleSave = async () => {
  const success = await userStore.updateProfile(form.value)
  if (success) {
    ElMessage.success('个人信息更新成功')
    dialogVisible.value = false
  } else {
    ElMessage.error('个人信息更新失败')
  }
}

const handleUpgrade = async () => {
  const success = await userStore.updateSubscription({ type: 'pro' })
  if (success) {
    ElMessage.success('订阅升级成功')
  } else {
    ElMessage.error('订阅升级失败')
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;

  .profile-card,
  .subscription-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .profile-content {
    display: flex;
    align-items: center;
    padding: 20px 0;

    .info-list {
      margin-left: 20px;

      .info-item {
        margin-bottom: 10px;

        .label {
          color: #606266;
          margin-right: 10px;
        }

        .value {
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }

  .benefits-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .el-icon {
        color: #67C23A;
        margin-right: 8px;
      }
    }
  }

  .upgrade-section {
    margin-top: 20px;
    text-align: center;
  }
}
</style>