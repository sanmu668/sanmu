<template>
  <div class="jobs-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>职位管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索职位..."
              style="width: 200px; margin-right: 16px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAddJob">发布职位</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="jobs" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="职位名称" width="200" />
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="location" label="工作地点" width="120" />
        <el-table-column prop="salary" label="薪资范围" width="120" />
        <el-table-column prop="experience" label="经验要求" width="100" />
        <el-table-column prop="education" label="学历要求" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '招聘中' : '已下线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              link 
              :type="row.status === 'active' ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'active' ? '下线' : '上线' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

interface Job {
  id: number
  title: string
  company: string
  location: string
  salary: string
  experience: string
  education: string
  status: 'active' | 'inactive'
  updateTime: string
}

// 表格数据
const jobs = ref<Job[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

// 获取职位列表
const fetchJobs = async () => {
  loading.value = true
  try {
    // TODO: 替换为实际的API调用
    const mockData: Job[] = Array.from({ length: 10 }, (_, index) => ({
      id: index + 1,
      title: `高级前端开发工程师${index + 1}`,
      company: '智简未来科技有限公司',
      location: '深圳',
      salary: '25k-35k',
      experience: '3-5年',
      education: '本科',
      status: (index % 2 === 0 ? 'active' : 'inactive') as 'active' | 'inactive',
      updateTime: new Date().toLocaleString()
    }))
    jobs.value = mockData
    total.value = 100
  } catch (error) {
    ElMessage.error('获取职位列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchJobs()
}

// 添加职位
const handleAddJob = () => {
  ElMessage.info('添加职位功能开发中')
}

// 编辑职位
const handleEdit = (row: any) => {
  ElMessage.info('编辑职位功能开发中')
}

// 切换职位状态
const handleToggleStatus = (row: any) => {
  const action = row.status === 'active' ? '下线' : '上线'
  ElMessageBox.confirm(
    `确定要${action}该职位吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    row.status = row.status === 'active' ? 'inactive' : 'active'
    ElMessage.success(`${action}成功`)
  }).catch(() => {})
}

// 删除职位
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除该职位吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 分页相关
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchJobs()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchJobs()
}

onMounted(() => {
  fetchJobs()
})
</script>

<style lang="scss" scoped>
.jobs-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      align-items: center;
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 