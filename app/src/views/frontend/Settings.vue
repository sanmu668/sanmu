<template>
  <div class="settings-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <!-- 个人信息卡片 -->
        <el-card class="user-card">
          <div class="avatar-container">
            <el-avatar :size="100" :src="userInfo.avatar" />
            <el-upload
              class="avatar-uploader"
              action="/api/upload/avatar"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
            >
              <el-button type="primary" link>更换头像</el-button>
            </el-upload>
          </div>
          <h3>{{ userInfo.name || userInfo.email }}</h3>
          <p class="user-email">{{ userInfo.email }}</p>
          <el-divider />
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.applicationCount }}</div>
              <div class="stat-label">投递数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.interviewCount }}</div>
              <div class="stat-label">面试数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.collectionCount }}</div>
              <div class="stat-label">收藏数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <!-- 基本信息设置 -->
        <el-card class="mb-20">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" @click="saveBasicInfo">保存修改</el-button>
            </div>
          </template>
          
          <el-form :model="basicForm" label-width="100px">
            <el-form-item label="昵称">
              <el-input v-model="basicForm.name" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="basicForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input
                v-model="basicForm.bio"
                type="textarea"
                :rows="3"
                placeholder="请输入个人简介"
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 密码修改 -->
        <el-card class="mb-20">
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
              <el-button type="primary" @click="changePassword">确认修改</el-button>
            </div>
          </template>
          
          <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="100px"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 通知设置 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>通知设置</span>
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
            </div>
          </template>
          
          <el-form :model="notificationForm">
            <el-form-item>
              <el-checkbox v-model="notificationForm.emailNotification">
                接收邮件通知
              </el-checkbox>
              <div class="form-tip">包括简历被查看、面试邀请等重要通知</div>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="notificationForm.recommendNotification">
                接收职位推荐
              </el-checkbox>
              <div class="form-tip">根据您的求职意向定期推送相关职位</div>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="notificationForm.activityNotification">
                接收活动通知
              </el-checkbox>
              <div class="form-tip">包括求职技巧分享、行业交流会等活动信息</div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

// 用户信息
const userInfo = reactive({
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  name: '张三',
  email: 'test@qq.com',
  applicationCount: 12,
  interviewCount: 3,
  collectionCount: 5
})

// 基本信息表单
const basicForm = reactive({
  name: userInfo.name,
  phone: '',
  bio: ''
})

// 密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 通知设置表单
const notificationForm = reactive({
  emailNotification: true,
  recommendNotification: true,
  activityNotification: false
})

// 更新头像
const handleAvatarSuccess = (response: any) => {
  userInfo.avatar = response.url
  ElMessage.success('头像更新成功')
}

// 保存基本信息
const saveBasicInfo = () => {
  ElMessage.success('基本信息保存成功')
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  })
}

// 保存通知设置
const saveNotificationSettings = () => {
  ElMessage.success('通知设置保存成功')
}
</script>

<style lang="scss" scoped>
.settings-container {
  .user-card {
    text-align: center;
    
    .avatar-container {
      margin: 20px 0;
      
      .avatar-uploader {
        margin-top: 10px;
      }
    }
    
    h3 {
      margin: 10px 0 5px;
    }
    
    .user-email {
      color: #909399;
      margin: 0;
    }
    
    .user-stats {
      display: flex;
      justify-content: space-around;
      padding: 10px 0;
      
      .stat-item {
        .stat-value {
          font-size: 20px;
          font-weight: bold;
          color: var(--el-color-primary);
        }
        
        .stat-label {
          font-size: 12px;
          color: #909399;
          margin-top: 5px;
        }
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    margin-left: 24px;
  }
}
</style> 