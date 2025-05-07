<template>
  <div class="applications-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>投递管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索投递记录..."
            style="width: 200px"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>
      
      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="jobName" label="职位名称" width="200" />
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="userName" label="申请人" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submittedTime" label="投递时间" width="180" />
        <el-table-column prop="updatedTime" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleUpdateStatus(row)">更新状态</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 状态更新对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="更新申请状态"
      width="30%"
    >
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="statusForm.status" placeholder="请选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="statusForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStatusSubmit">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import axios from 'axios'

interface Application {
  id: number
  resumeId: number
  jobId: number
  userId: number
  userName: string
  jobName: string
  company: string
  salary_range: string | null
  status: string
  submittedTime: string
  updatedTime: string
}

// 状态选项
const statusOptions = [
  { label: '待处理', value: '待处理' },
  { label: '简历筛选', value: '简历筛选' },
  { label: '面试中', value: '面试中' },
  { label: '已录用', value: '已录用' },
  { label: '已拒绝', value: '已拒绝' }
]

// 表格数据
const applications = ref<Application[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

// 状态更新对话框
const statusDialogVisible = ref(false)
const statusForm = ref({
  status: '',
  remark: ''
})
const currentApplication = ref<Application | null>(null)

// 获取状态标签类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '待处理': 'info',
    '简历筛选': 'warning',
    '面试中': 'primary',
    '已录用': 'success',
    '已拒绝': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取投递列表
const fetchApplications = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/admin/applications', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        search: searchQuery.value
      }
    })
    
    // 检查响应数据格式并打印，用于调试
    console.log('API Response:', response.data)
    
    // 如果响应是数组，直接使用
    if (Array.isArray(response.data)) {
      applications.value = response.data
      total.value = response.data.length
    } 
    // 如果响应是单个对象，转换为数组
    else if (typeof response.data === 'object' && response.data !== null) {
      applications.value = [response.data]
      total.value = 1
    } 
    // 如果响应包含分页信息
    else if (response.data?.content) {
      applications.value = response.data.content
      total.value = response.data.totalElements
    }
    else {
      applications.value = []
      total.value = 0
      console.warn('Unexpected API response format:', response.data)
    }
  } catch (error: any) {
    console.error('Error fetching applications:', error)
    ElMessage.error(`获取投递列表失败: ${error?.message || '未知错误'}`)
    applications.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchApplications()
}

// 查看详情
const handleView = (row: Application) => {
  ElMessage.info('查看详情功能开发中')
}

// 更新状态
const handleUpdateStatus = (row: Application) => {
  currentApplication.value = row
  statusForm.value.status = row.status
  statusForm.value.remark = ''
  statusDialogVisible.value = true
}

// 提交状态更新
const handleStatusSubmit = async () => {
  if (!currentApplication.value) return
  
  try {
    await axios.put(`http://localhost:8080/api/admin/applications/${currentApplication.value.id}/status`, {
      status: statusForm.value.status,
      remark: statusForm.value.remark
    })
    
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    fetchApplications() // 刷新列表
  } catch (error) {
    ElMessage.error('状态更新失败')
    console.error('Error updating application status:', error)
  }
}

// 分页相关
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchApplications()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchApplications()
}

onMounted(() => {
  fetchApplications()
})
</script>

<style lang="scss" scoped>
.applications-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}
</style> 