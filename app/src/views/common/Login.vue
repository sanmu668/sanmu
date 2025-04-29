<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>智简未来</h2>
          <p>智能招聘系统</p>
        </div>
      </template>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0" size="large">
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？<el-button type="text" @click="goToRegister">立即注册</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { login } from '@/api/auth'
import { parseJwt } from '@/utils/auth'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref<FormInstance>()

const loginForm = reactive({
  email: '',
  password: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true
    //调用后端真实接口
    const res = await login({
      email: loginForm.email,
      password: loginForm.password
    })

    if (!res || !res.token) {
      ElMessage.error("登入失败，未返回token")
      return
    }

    const token = res.token
    //保存token
    localStorage.setItem('token', token)

    //解析token获取用户信息
    const userInfo = parseJwt(token)
    console.log('Token解析结果：', userInfo)
    
    if (!userInfo) {
      ElMessage.error("登入失败，token解析失败")
      return
    }

    //保存用户信息
    localStorage.setItem('userInfo', JSON.stringify({
      email: userInfo.sub,
      role: userInfo.role
    }))

    //根据角色跳转不同页面
    if (userInfo.role === 'admin') {
      ElMessage.success('管理员登入成功')
      router.push('/admin')
    } else {
      ElMessage.success('登入成功')
      router.push('/')
    }
  } catch(error) {
    console.error('登入失败', error)
    ElMessage.error('登入失败，请检查邮箱和密码')
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .login-card {
    width: 400px;
    
    .card-header {
      text-align: center;
      
      h2 {
        margin: 0;
        color: #409EFF;
        font-size: 24px;
      }
      
      p {
        margin: 10px 0 0;
        color: #909399;
        font-size: 14px;
      }
    }
    
    .login-button {
      width: 100%;
    }

    .register-link {
      text-align: center;
      margin-top: 15px;
      color: #606266;
    }
  }
}
</style> 