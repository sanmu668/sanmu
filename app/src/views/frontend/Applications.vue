<template>
  <div class="applications-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投递记录</span>
          <el-radio-group v-model="status" size="small">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="pending">待处理</el-radio-button>
            <el-radio-button label="reviewing">审核中</el-radio-button>
            <el-radio-button label="interview">面试中</el-radio-button>
            <el-radio-button label="offer">已录用</el-radio-button>
            <el-radio-button label="rejected">已拒绝</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table
        :data="applicationList"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="jobTitle" label="职位名称" min-width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewJobDetail(row)">
              {{ row.jobTitle }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="company" label="公司" min-width="150" />
        <el-table-column prop="salary" label="薪资" width="120" />
        <el-table-column prop="applyTime" label="投递时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="viewProgress(row)"
              :disabled="row.status === 'rejected'"
            >
              查看进度
            </el-button>
            <el-button
              type="danger"
              link
              @click="cancelApplication(row)"
              v-if="row.status === 'pending'"
            >
              撤销投递
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 进度详情对话框 -->
    <el-dialog
      v-model="progressVisible"
      title="投递进度"
      width="600px"
      destroy-on-close
    >
      <el-timeline v-if="currentApplication">
        <el-timeline-item
          v-for="(activity, index) in currentApplication.progress"
          :key="index"
          :type="activity.type"
          :color="activity.color"
          :timestamp="activity.time"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Progress {
  type: string
  color: string
  time: string
  content: string
}

interface Application {
  id: number
  jobTitle: string
  company: string
  salary: string
  applyTime: string
  status: string
  progress: Progress[]
}

const loading = ref(false)
const status = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟数据
const applicationList = ref<Application[]>([
  {
    id: 1,
    jobTitle: '高级前端开发工程师',
    company: '智简科技有限公司',
    salary: '25k-35k',
    applyTime: '2024-03-15 10:30:00',
    status: 'reviewing',
    progress: [
      {
        type: 'success',
        color: '#0bbd87',
        time: '2024-03-15 10:30:00',
        content: '简历投递成功'
      },
      {
        type: 'primary',
        color: '#409EFF',
        time: '2024-03-15 14:20:00',
        content: 'HR开始审核简历'
      }
    ]
  },
  {
    id: 2,
    jobTitle: '后端开发工程师',
    company: '未来科技有限公司',
    salary: '20k-30k',
    applyTime: '2024-03-14 15:45:00',
    status: 'interview',
    progress: [
      {
        type: 'success',
        color: '#0bbd87',
        time: '2024-03-14 15:45:00',
        content: '简历投递成功'
      },
      {
        type: 'success',
        color: '#0bbd87',
        time: '2024-03-14 16:30:00',
        content: '简历通过初筛'
      },
      {
        type: 'primary',
        color: '#409EFF',
        time: '2024-03-15 09:00:00',
        content: '安排面试：2024-03-16 14:00 线上面试'
      }
    ]
  }
])

// 状态相关方法
const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'info',
    reviewing: 'warning',
    interview: 'primary',
    offer: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待处理',
    reviewing: '审核中',
    interview: '面试中',
    offer: '已录用',
    rejected: '已拒绝'
  }
  return map[status] || '未知'
}

// 分页方法
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadData()
}

// 加载数据
const loadData = () => {
  loading.value = true
  // 模拟接口调用
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 查看职位详情
const viewJobDetail = (row: Application) => {
  console.log('查看职位详情:', row)
}

// 进度详情
const progressVisible = ref(false)
const currentApplication = ref<Application | null>(null)

const viewProgress = (row: Application) => {
  currentApplication.value = row
  progressVisible.value = true
}

// 撤销投递
const cancelApplication = (row: Application) => {
  ElMessageBox.confirm(
    '确定要撤销该职位的投递吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('撤销成功')
    loadData()
  })
}
</script>

<style lang="scss" scoped>
.applications-container {
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