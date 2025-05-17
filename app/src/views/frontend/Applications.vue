<template>
  <div class="applications-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投递记录</span>
          <el-radio-group v-model="status" size="small">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="pending">待处理</el-radio-button>
            <el-radio-button value="reviewing">审核中</el-radio-button>
            <el-radio-button value="interview">面试中</el-radio-button>
            <el-radio-button value="offer">已录用</el-radio-button>
            <el-radio-button value="rejected">已拒绝</el-radio-button>
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-dropdown trigger="click">
              <el-button type="primary" link>
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="viewProgress(row)" :disabled="row.status === 'rejected'">
                    <el-icon><View /></el-icon>
                    查看进度
                  </el-dropdown-item>
                  <el-dropdown-item 
                    @click="cancelApplication(row)" 
                    v-if="row.status === 'pending'"
                    type="danger"
                  >
                    <el-icon><Delete /></el-icon>
                    撤销投递
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MoreFilled, View, Delete } from '@element-plus/icons-vue'
import request from '@/api/request'
import { API_ENDPOINTS } from '@/config'

// 导出图标组件供模板使用
const icons = {
  MoreFilled,
  View,
  Delete
}

interface Progress {
  type: string
  color: string
  time: string
  content: string
}

interface Application {
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
const total = ref(0)
const applicationList = ref<Application[]>([])

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await request.get(API_ENDPOINTS.USER.APPLICATIONS)
    
    if (response.data && Array.isArray(response.data)) {
      // 转换后端数据格式为前端所需格式
      applicationList.value = response.data.map((item: any) => ({
        jobTitle: item.jobName,
        company: item.company,
        salary: item.salary_range,
        applyTime: item.submittedTime,
        status: mapStatus(item.status),
        progress: [
          {
            type: 'success',
            color: '#0bbd87',
            time: item.submittedTime,
            content: '简历投递成功'
          }
        ]
      }))
      total.value = response.data.length
    } else if (response.data) {
      // 如果只返回了单个对象，将其转换为数组
      applicationList.value = [{
        jobTitle: response.data.jobName,
        company: response.data.company,
        salary: response.data.salary_range,
        applyTime: response.data.submittedTime,
        status: mapStatus(response.data.status),
        progress: [
          {
            type: 'success',
            color: '#0bbd87',
            time: response.data.submittedTime,
            content: '简历投递成功'
          }
        ]
      }]
      total.value = 1
    } else {
      applicationList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取投递记录失败:', error)
    ElMessage.error('获取投递记录失败')
    applicationList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 状态映射函数
const mapStatus = (status: string): string => {
  const statusMap: Record<string, string> = {
    '待处理': 'pending',
    '审核中': 'reviewing',
    '面试中': 'interview',
    '已录用': 'offer',
    '已拒绝': 'rejected'
  }
  return statusMap[status] || status
}

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

// 监听状态变化
watch(status, () => {
  currentPage.value = 1
  loadData()
})

// 初始化加载
onMounted(() => {
  loadData()
})

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