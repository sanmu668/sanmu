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
        <el-table-column prop="jobTitle" label="职位名称" width="200" />
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="applicant" label="申请人" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="投递时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
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

interface Application {
  id: number
  jobTitle: string
  company: string
  applicant: string
  status: ApplicationStatus
  applyTime: string
  updateTime: string
}

type ApplicationStatus = 'pending' | 'reviewing' | 'interview' | 'offer' | 'rejected'

interface StatusOption {
  label: string
  value: ApplicationStatus
}

// 状态选项
const statusOptions: StatusOption[] = [
  { label: '待处理', value: 'pending' },
  { label: '简历筛选', value: 'reviewing' },
  { label: '面试中', value: 'interview' },
  { label: '已录用', value: 'offer' },
  { label: '已拒绝', value: 'rejected' }
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
  status: '' as ApplicationStatus,
  remark: ''
})
const currentApplication = ref<Application | null>(null)

// 获取状态标签类型
const getStatusType = (status: ApplicationStatus) => {
  const typeMap: Record<ApplicationStatus, string> = {
    pending: 'info',
    reviewing: 'warning',
    interview: 'primary',
    offer: 'success',
    rejected: 'danger'
  }
  return typeMap[status]
}

// 获取状态文本
const getStatusText = (status: ApplicationStatus) => {
  const textMap: Record<ApplicationStatus, string> = {
    pending: '待处理',
    reviewing: '简历筛选',
    interview: '面试中',
    offer: '已录用',
    rejected: '已拒绝'
  }
  return textMap[status]
}

// 获取投递列表
const fetchApplications = async () => {
  loading.value = true
  try {
    // TODO: 替换为实际的API调用
    const mockData: Application[] = Array.from({ length: 10 }, (_, index) => ({
      id: index + 1,
      jobTitle: `高级前端开发工程师${index + 1}`,
      company: '智简未来科技有限公司',
      applicant: `申请人${index + 1}`,
      status: ['pending', 'reviewing', 'interview', 'offer', 'rejected'][index % 5] as ApplicationStatus,
      applyTime: new Date(Date.now() - Math.random() * 10 * 24 * 60 * 60 * 1000).toLocaleString(),
      updateTime: new Date().toLocaleString()
    }))
    applications.value = mockData
    total.value = 100
  } catch (error) {
    ElMessage.error('获取投递列表失败')
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
const handleStatusSubmit = () => {
  if (!currentApplication.value) return
  
  // TODO: 替换为实际的API调用
  currentApplication.value.status = statusForm.value.status
  currentApplication.value.updateTime = new Date().toLocaleString()
  
  ElMessage.success('状态更新成功')
  statusDialogVisible.value = false
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