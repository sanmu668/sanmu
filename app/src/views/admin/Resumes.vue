<template>
  <div class="resumes-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>简历管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索简历..."
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
      
      <el-table :data="resumes" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="title" label="简历标题" width="200" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="experience" label="工作经验" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
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
import axios from 'axios'

// Configure axios base URL
axios.defaults.baseURL = 'http://localhost:8080'

interface Resume {
  id: number
  userName: string | null
  title: string
  education: string | null
  experience: string | null
  status: string | null
  updateTime: string
}

// 表格数据
const resumes = ref<Resume[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

// 获取简历列表
const fetchResumes = async () => {
  loading.value = true
  try {
    const response = await axios.get(`/api/admin/resumes`, {
      params: {
        page: currentPage.value , 
        size: pageSize.value,
        query: searchQuery.value || undefined
      }
    })
    resumes.value = response.data.content
    total.value = response.data.totalElements || response.data.content.length
  } catch (error) {
    ElMessage.error('获取简历列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResumes()
}

// 查看简历
const handleView = (row: Resume) => {
  const pdfUrl = `${axios.defaults.baseURL}/api/admin/resumes/pdf/${row.id}`
  try {
    // For PDF files, we should use an iframe or embed element for better viewing experience
    const width = 850
    const height = 600
    const left = (window.screen.width / 2) - (width / 2)
    const top = (window.screen.height / 2) - (height / 2)
    
    window.open(
      pdfUrl,
      '_blank',
      `toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no, width=${width}, height=${height}, top=${top}, left=${left}`
    )
  } catch (error) {
    ElMessage.error('打开PDF失败')
  }
}

// 下载简历
const handleDownload = async (row: Resume) => {
  try {
    const response = await axios.get(`/api/admin/resumes/${row.id}/download`, {
      responseType: 'blob'
    })
    
    // Check if we got a valid response
    if (!(response.data instanceof Blob)) {
      throw new Error('Invalid response format')
    }
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${row.title || 'resume'}.pdf`) // Add fallback name
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('Download error:', error)
    ElMessage.error(error instanceof Error ? error.message : '下载失败，请稍后重试')
  }
}

// 删除简历
const handleDelete = (row: Resume) => {
  ElMessageBox.confirm(
    '确定要删除该简历吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await axios.delete(`/api/admin/resumes/${row.id}`)
      ElMessage.success('删除成功')
      fetchResumes() // 重新加载列表
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 分页相关
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchResumes()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchResumes()
}

onMounted(() => {
  fetchResumes()
})
</script>

<style lang="scss" scoped>
.resumes-management {
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
}
</style> 