<template>
  <div class="resume-container">
    <div class="resume-header">
      <h2>我的简历</h2>
      <el-button type="primary" @click="navigateToResumeModel">创建简历</el-button>
    </div>

    <div class="resume-list">
      <el-table 
        v-loading="loading"
        :data="resumeList" 
        style="width: 100%"
        :empty-text="error || '暂无简历数据'"
      >
        <el-table-column prop="title" label="名称" />
        <el-table-column prop="updateTime" label="更新日期">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="editResume(row)">编辑</el-button>
            <el-button type="success" link @click="viewResume(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { openPdfPreview } from '@/utils/pdfPreview'

const router = useRouter()
const loading = ref(false)
const error = ref('')

interface ResumeItem {
  id: number
  userName: string
  title: string
  education: string | null
  experience: string | null
  status: string | null
  updateTime: string
}

const resumeList = ref<ResumeItem[]>([])

const fetchResumeList = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(`/api/user/resumes`, {
      baseURL: 'http://localhost:8080',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'accept': '*/*'
      }
    })
    
    if (response.data) {
      resumeList.value = Array.isArray(response.data) ? response.data : [response.data]
    } else {
      error.value = '获取数据失败'
    }
  } catch (err: any) {
    console.error('获取简历列表失败:', err)
    error.value = err.message || '获取简历列表失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const navigateToResumeModel = () => {
  router.push('/resumeModel/cool')
}

const editResume = (resume: ResumeItem) => {
  router.push(`/resume/edit/${resume.id}`)
}

const viewResume = async (row: ResumeItem) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('未登录或登录已过期')
      return
    }

    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const pdfUrl = `${baseURL}/api/user/resumes/pdf/${row.id}`

    openPdfPreview({
      title: row.title,
      pdfUrl,
      token
    })
  } catch (error) {
    console.error('预览PDF失败:', error)
    ElMessage.error('打开PDF预览失败')
  }
}

const handleDelete = (resume: ResumeItem) => {
  ElMessageBox.confirm(
    '确定要删除这份简历吗？此操作不可恢复',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const token = localStorage.getItem('token')
      await axios.delete(`/api/user/resumes/${resume.id}`, {
        baseURL: 'http://localhost:8080',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        }
      })
      ElMessage.success('删除成功')
      fetchResumeList()
    } catch (err: any) {
      ElMessage.error('删除失败：' + (err.message || '未知错误'))
    }
  }).catch(() => {
    // 用户取消删除操作
  })
}

onMounted(() => {
  fetchResumeList()
})
</script>

<style lang="scss" scoped>
.resume-container {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 40px);
}

.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h2 {
    margin: 0;
    font-size: 24px;
    font-weight: normal;
  }
}

.resume-list {
  background-color: #fff;
  border-radius: 4px;
}

:deep(.el-table) {
  --el-table-border-color: #ebeef5;
  --el-table-header-background-color: #f5f7fa;
}
</style> 