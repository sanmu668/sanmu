<template>
  <div class="settings-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <!-- 个人信息卡片 -->
        <el-card class="user-card" v-loading="loading.profile">
          <div class="avatar-container">
            <el-avatar :size="100" :src="userInfo.photo ? `data:image/jpeg;base64,${userInfo.photo}` : defaultAvatar" />
            <el-upload
              class="avatar-uploader"
              action="/api/user/personal/photo"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
            >
              <el-button type="primary" link>更换头像</el-button>
            </el-upload>
          </div>
          <h3>{{ userInfo.username || userInfo.email }}</h3>
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
        <el-card class="mb-20" v-loading="loading.basicInfo">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" @click="saveBasicInfo" :loading="loading.basicInfo">
                保存修改
              </el-button>
            </div>
          </template>
          
          <el-form :model="basicForm" label-width="100px" ref="basicFormRef">
            <el-form-item label="昵称" prop="username">
              <el-input v-model="basicForm.username" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号码" prop="number">
              <el-input v-model="basicForm.number" placeholder="请输入手机号码" />
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
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
        <el-card class="mb-20" v-loading="loading.password">
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
              <el-button type="primary" @click="changePassword" :loading="loading.password">
                确认修改
              </el-button>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/api/request'

// 默认头像
const defaultAvatar = 'path/to/default/avatar.png'

// 上传头像的headers
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

// Loading states
const loading = reactive({
  profile: false,
  basicInfo: false,
  password: false
})

// 用户信息
const userInfo = reactive({
  photo: '',
  username: '',
  email: '',
  number: '',
  applicationCount: 0,
  interviewCount: 0,
  collectionCount: 0
})

// 基本信息表单
const basicFormRef = ref<FormInstance>()
const basicForm = reactive({
  username: '',
  number: '',
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
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' },
    { max: 20, message: '密码长度不能超过20位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' },
    { max: 20, message: '密码长度不能超过20位', trigger: 'blur' }
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

const router = useRouter()

// 获取用户信息
const fetchUserProfile = async () => {
  loading.profile = true
  try {
    const response = await request.get('/api/user/personal/profile')
    const data = response.data
    Object.assign(userInfo, data)
    basicForm.username = data.username
    basicForm.number = data.number
    basicForm.bio = data.bio || ''
  } catch (error: any) {
    console.error('Error fetching user profile:', error.response || error)
    const errorMessage = error.response?.data?.message 
      || error.response?.data 
      || error.message 
      || '获取用户信息失败'
    ElMessage.error(errorMessage)
    
    if (error.response?.status === 401) {
      router.push('/login')
    }
  } finally {
    loading.profile = false
  }
}

// 更新头像前的验证
const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 更新头像成功
const handleAvatarSuccess = (response: any) => {
  userInfo.photo = response.data
  ElMessage.success('头像更新成功')
}

// 更新头像失败
const handleAvatarError = () => {
  ElMessage.error('头像上传失败')
}

// 保存基本信息
const saveBasicInfo = async () => {
  if (!basicFormRef.value) return
  
  loading.basicInfo = true
  try {
    await request.put('/api/user/personal/basic-info', basicForm)
    ElMessage.success('基本信息保存成功')
    await fetchUserProfile() // 刷新用户信息
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    loading.basicInfo = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    // Validate password format before sending
    if (passwordForm.newPassword === passwordForm.oldPassword) {
      ElMessage.error('新密码不能与当前密码相同')
      return
    }
    
    loading.password = true
    
    const passwordData = {
      oldPassword: passwordForm.oldPassword.trim(),
      newPassword: passwordForm.newPassword.trim(),
      confirmPassword: passwordForm.confirmPassword.trim()
    }
    console.log('Password change request data:', passwordData)
    
    await request.put('/api/user/personal/password', passwordData)
    
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error: any) {
    if (error.response) {
      console.error('Password change error:', error.response.data)
      const errorMessage = error.response.data.message 
        || error.response.data.error 
        || '密码修改失败，请确保密码符合要求并且正确输入当前密码'
      ElMessage.error(errorMessage)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('密码修改失败，请稍后重试')
    }
  } finally {
    loading.password = false
  }
}

// 保存通知设置
const saveNotificationSettings = () => {
  ElMessage.success('通知设置保存成功')
}

// 页面加载时获取用户信息
onMounted(() => {
  fetchUserProfile()
})
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