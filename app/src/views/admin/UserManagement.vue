<template>
  <div class="user-management">
    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            搜索
          </el-button>
          <el-button @click="resetSearch">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="photoBase64" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar 
              :size="40" 
              :src="row.photoBase64 ? `data:image/jpeg;base64,${row.photoBase64}` : ''" 
            />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="number" label="号码" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="号码" prop="number">
          <el-input v-model="userForm.number" placeholder="请输入号码" />
        </el-form-item>
        <el-form-item label="密码" prop="passwordHash" v-if="dialogType === 'add'">
          <el-input
            v-model="userForm.passwordHash"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createUser, getAllUsers, deleteUser, getUserByUsername, getUserByEmail, updateUser } from '@/api/userService'
import type { User, UserDTO } from '@/types/user'

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
})

// 用户列表数据
const loading = ref(false)
const userList: Ref<UserDTO[]> = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const userFormRef = ref<FormInstance>()
const userForm = reactive<User>({
  username: '',
  email: '',
  number: '',
  passwordHash: ''
})

// 表单验证规则
const userFormRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  number: [
    { required: true, message: '请输入号码', trigger: 'blur' }
  ],
  passwordHash: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 搜索处理
const handleSearch = async () => {
  loading.value = true
  try {
    let data
    if (searchForm.username) {
      data = await getUserByUsername(searchForm.username)
      if (Array.isArray(data)) {
        userList.value = data
        total.value = data.length
      } else {
        userList.value = []
        total.value = 0
      }
    } else if (searchForm.email) {
      data = await getUserByEmail(searchForm.email)
      // 邮箱搜索返回单个对象
      if (data) {
        userList.value = [data]
        total.value = 1
      } else {
        userList.value = []
        total.value = 0
      }
    } else {
      data = await getAllUsers()
      if (Array.isArray(data)) {
        userList.value = data
        total.value = data.length
      } else {
        userList.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
    userList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.username = ''
  searchForm.email = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchUserList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchUserList()
}

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const response = await getAllUsers()
    if (Array.isArray(response)) {
      userList.value = response
      total.value = response.length
    } else {
      ElMessage.error('获取用户列表数据格式错误')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  userForm.username = ''
  userForm.email = ''
  userForm.number = ''
  userForm.passwordHash = ''
}

// 编辑用户
const handleEdit = (row: UserDTO) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    email: row.email,
    number: row.number
  })
  userForm.passwordHash = '' // 清空密码字段
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          // 创建新用户时，确保包含所有必要字段
          const now = new Date().toISOString()
          const newUser = {
            ...userForm,
            role: 'user',
            createdAt: now,
            updateAt: now,
            lastLogin: now,
            photo: null
          }
          await createUser(newUser)
          ElMessage.success('添加成功')
        } else {
          const updateData = {
            username: userForm.username,
            email: userForm.email,
            number: userForm.number,
            updateAt: new Date().toISOString()
          }
          await updateUser(userForm.id!, updateData)
          ElMessage.success('修改成功')
        }
        dialogVisible.value = false
        fetchUserList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '修改失败')
      }
    }
  })
}

// 删除用户
const handleDelete = (row: UserDTO) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${row.username} 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      fetchUserList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 初始化
onMounted(() => {
  fetchUserList()
})
</script>

<style lang="scss" scoped>
.user-management {
  .search-card {
    margin-bottom: 20px;
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }
  }
  
  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

:deep(.el-button-group) {
  .el-button {
    margin-left: 0 !important;
  }
}
</style> 