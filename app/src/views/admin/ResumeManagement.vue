<template>
  <div class="resume-management">
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
        <el-form-item label="简历标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入简历标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 简历列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>简历列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleBatchApprove" :disabled="!selectedResumes.length">
              <el-icon><Check /></el-icon>
              批量通过
            </el-button>
            <el-button type="danger" @click="handleBatchReject" :disabled="!selectedResumes.length">
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="resumeList"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="title" label="简历标题" min-width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handlePreview(row)">
              {{ row.title }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="AI评分" width="100">
          <template #default="{ row }">
            <el-rate
              v-model="row.score"
              :max="5"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handlePreview(row)"
              >
                预览
              </el-button>
              <el-button
                type="success"
                link
                @click="handleApprove(row)"
                v-if="row.status === 'pending'"
              >
                通过
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleReject(row)"
                v-if="row.status === 'pending'"
              >
                拒绝
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

    <!-- 简历预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="简历预览"
      width="800px"
      class="resume-preview-dialog"
    >
      <div class="resume-preview" v-loading="previewLoading">
        <div class="preview-header">
          <h2>{{ currentResume?.title }}</h2>
          <div class="meta-info">
            <span>更新时间：{{ currentResume?.updateTime }}</span>
            <span>AI评分：
              <el-rate
                v-if="currentResume"
                v-model="currentResume.score"
                :max="5"
                disabled
                show-score
                text-color="#ff9900"
              />
            </span>
          </div>
        </div>
        <el-divider />
        <div class="preview-content" v-if="currentResume">
          <!-- 这里根据实际简历数据结构展示内容 -->
          <div class="section">
            <h3>基本信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <label>姓名：</label>
                <span>{{ currentResume.basicInfo?.name }}</span>
              </div>
              <div class="info-item">
                <label>年龄：</label>
                <span>{{ currentResume.basicInfo?.age }}</span>
              </div>
              <div class="info-item">
                <label>学历：</label>
                <span>{{ currentResume.basicInfo?.education }}</span>
              </div>
              <div class="info-item">
                <label>工作年限：</label>
                <span>{{ currentResume.basicInfo?.experience }}</span>
              </div>
            </div>
          </div>
          
          <div class="section">
            <h3>求职意向</h3>
            <div class="info-grid">
              <div class="info-item">
                <label>期望职位：</label>
                <span>{{ currentResume.intention?.position }}</span>
              </div>
              <div class="info-item">
                <label>期望薪资：</label>
                <span>{{ currentResume.intention?.salary }}</span>
              </div>
              <div class="info-item">
                <label>工作城市：</label>
                <span>{{ currentResume.intention?.city }}</span>
              </div>
            </div>
          </div>

          <div class="section">
            <h3>工作经历</h3>
            <el-timeline>
              <el-timeline-item
                v-for="(exp, index) in currentResume.experience"
                :key="index"
                :timestamp="exp.period"
                placement="top"
              >
                <h4>{{ exp.company }} - {{ exp.position }}</h4>
                <p>{{ exp.description }}</p>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewVisible = false">关闭</el-button>
          <template v-if="currentResume?.status === 'pending'">
            <el-button type="success" @click="handleApprove(currentResume)">通过</el-button>
            <el-button type="danger" @click="handleReject(currentResume)">拒绝</el-button>
          </template>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close } from '@element-plus/icons-vue'

interface BasicInfo {
  name: string
  age: number
  education: string
  experience: string
}

interface JobIntention {
  position: string
  salary: string
  city: string
}

interface WorkExperience {
  company: string
  position: string
  period: string
  description: string
}

interface Resume {
  id: number
  username: string
  title: string
  updateTime: string
  status: 'pending' | 'approved' | 'rejected'
  score: number
  basicInfo?: BasicInfo
  intention?: JobIntention
  experience?: WorkExperience[]
}

// 搜索表单
const searchForm = reactive({
  username: '',
  title: '',
  status: ''
})

// 简历列表数据
const loading = ref(false)
const resumeList: Ref<Resume[]> = ref([
  {
    id: 1,
    username: 'user1',
    title: '前端开发工程师简历',
    updateTime: '2024-03-20 10:00:00',
    status: 'pending',
    score: 4.5,
    basicInfo: {
      name: '张三',
      age: 28,
      education: '本科',
      experience: '5年'
    },
    intention: {
      position: '高级前端开发工程师',
      salary: '25-35K',
      city: '深圳'
    },
    experience: [
      {
        company: 'XX科技有限公司',
        position: '前端开发工程师',
        period: '2020-01 至 2024-03',
        description: '负责公司核心产品的前端开发工作，包括需求分析、技术方案设计和具体实现。'
      }
    ]
  }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 预览相关
const previewVisible = ref(false)
const previewLoading = ref(false)
const currentResume: Ref<Resume | null> = ref(null)
const selectedResumes = ref<Resume[]>([])

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return textMap[status] || status
}

// 搜索处理
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('Search form:', searchForm)
}

// 重置搜索
const resetSearch = () => {
  searchForm.username = ''
  searchForm.title = ''
  searchForm.status = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchResumeList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchResumeList()
}

// 获取简历列表
const fetchResumeList = () => {
  loading.value = true
  // TODO: 实现获取简历列表的接口调用
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 预览简历
const handlePreview = (resume: Resume) => {
  currentResume.value = resume
  previewVisible.value = true
  previewLoading.value = true
  // TODO: 获取完整的简历详情
  setTimeout(() => {
    previewLoading.value = false
  }, 500)
}

// 处理选择变化
const handleSelectionChange = (selection: Resume[]) => {
  selectedResumes.value = selection
}

// 通过简历
const handleApprove = (resume: Resume) => {
  ElMessageBox.confirm(
    `确定要通过该简历吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(() => {
    // TODO: 实现通过简历的接口调用
    resume.status = 'approved'
    ElMessage.success('操作成功')
    previewVisible.value = false
  })
}

// 拒绝简历
const handleReject = (resume: Resume) => {
  ElMessageBox.confirm(
    `确定要拒绝该简历吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现拒绝简历的接口调用
    resume.status = 'rejected'
    ElMessage.success('操作成功')
    previewVisible.value = false
  })
}

// 批量通过
const handleBatchApprove = () => {
  if (!selectedResumes.value.length) return
  
  ElMessageBox.confirm(
    `确定要通过选中的 ${selectedResumes.value.length} 份简历吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(() => {
    // TODO: 实现批量通过的接口调用
    selectedResumes.value.forEach(resume => {
      resume.status = 'approved'
    })
    ElMessage.success('操作成功')
  })
}

// 批量拒绝
const handleBatchReject = () => {
  if (!selectedResumes.value.length) return
  
  ElMessageBox.confirm(
    `确定要拒绝选中的 ${selectedResumes.value.length} 份简历吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现批量拒绝的接口调用
    selectedResumes.value.forEach(resume => {
      resume.status = 'rejected'
    })
    ElMessage.success('操作成功')
  })
}

// 初始化
fetchResumeList()
</script>

<style lang="scss" scoped>
.resume-management {
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
      
      .header-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.resume-preview-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
  
  .resume-preview {
    padding: 20px;
    
    .preview-header {
      h2 {
        margin: 0 0 10px;
      }
      
      .meta-info {
        display: flex;
        gap: 20px;
        color: #666;
      }
    }
    
    .section {
      margin-bottom: 24px;
      
      h3 {
        margin: 0 0 16px;
        padding-left: 10px;
        border-left: 4px solid var(--el-color-primary);
      }
      
      .info-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        
        .info-item {
          label {
            color: #666;
            margin-right: 8px;
          }
        }
      }
    }
  }
}

:deep(.el-button-group) {
  .el-button {
    margin-left: 0 !important;
  }
}
</style> 