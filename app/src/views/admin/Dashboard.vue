<template>
  <div class="dashboard-container">
    <!-- 数据卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-content">
            <div class="icon-wrapper blue">
              <el-icon><User /></el-icon>
            </div>
            <div class="data-info">
              <div class="value">{{ statistics.totalUsers }}</div>
              <div class="label">总用户数</div>
            </div>
          </div>
          <div class="card-footer">
            <span>较昨日</span>
            <span :class="['change', statistics.userChange >= 0 ? 'up' : 'down']">
              {{ statistics.userChange > 0 ? '+' : '' }}{{ statistics.userChange }}
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-content">
            <div class="icon-wrapper green">
              <el-icon><Document /></el-icon>
            </div>
            <div class="data-info">
              <div class="value">{{ statistics.totalResumes }}</div>
              <div class="label">简历数量</div>
            </div>
          </div>
          <div class="card-footer">
            <span>较昨日</span>
            <span :class="['change', statistics.resumeChange >= 0 ? 'up' : 'down']">
              {{ statistics.resumeChange > 0 ? '+' : '' }}{{ statistics.resumeChange }}
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-content">
            <div class="icon-wrapper orange">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div class="data-info">
              <div class="value">{{ statistics.totalJobs }}</div>
              <div class="label">职位数量</div>
            </div>
          </div>
          <div class="card-footer">
            <span>较昨日</span>
            <span :class="['change', statistics.jobChange >= 0 ? 'up' : 'down']">
              {{ statistics.jobChange > 0 ? '+' : '' }}{{ statistics.jobChange }}
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <div class="card-content">
            <div class="icon-wrapper purple">
              <el-icon><List /></el-icon>
            </div>
            <div class="data-info">
              <div class="value">{{ statistics.totalApplications }}</div>
              <div class="label">投递数量</div>
            </div>
          </div>
          <div class="card-footer">
            <span>较昨日</span>
            <span :class="['change', statistics.applicationChange >= 0 ? 'up' : 'down']">
              {{ statistics.applicationChange > 0 ? '+' : '' }}{{ statistics.applicationChange }}
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-radio-group v-model="userChartType" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <!-- 这里使用 ECharts 组件 -->
            <div ref="userChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>投递状态分布</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- 这里使用 ECharts 组件 -->
            <div ref="pieChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in recentActivities"
          :key="index"
          :timestamp="activity.time"
          :type="activity.type"
        >
          {{ activity.content }}
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { User, Document, Briefcase, List } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 统计数据
const statistics = ref({
  totalUsers: 1234,
  userChange: 56,
  totalResumes: 890,
  resumeChange: -12,
  totalJobs: 345,
  jobChange: 23,
  totalApplications: 678,
  applicationChange: 45
})

// 图表相关
const userChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
let userChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
const userChartType = ref('week')

// 最近活动
const recentActivities = ref([
  {
    content: '新用户 张三 注册了账号',
    time: '2024-03-20 10:30:00',
    type: 'primary'
  },
  {
    content: '李四 投递了 高级前端开发 职位',
    time: '2024-03-20 09:45:00',
    type: 'success'
  },
  {
    content: '王五 上传了新的简历',
    time: '2024-03-20 09:30:00',
    type: 'info'
  },
  {
    content: 'HR已处理 赵六 的简历投递',
    time: '2024-03-20 09:00:00',
    type: 'warning'
  }
])

// 初始化用户增长趋势图表
const initUserChart = () => {
  if (!userChartRef.value) return
  
  userChart = echarts.init(userChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210]
      }
    ]
  }
  userChart.setOption(option)
}

// 初始化投递状态分布图表
const initPieChart = () => {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0
    },
    series: [
      {
        name: '投递状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 335, name: '待处理' },
          { value: 310, name: '已通过' },
          { value: 234, name: '面试中' },
          { value: 135, name: '已拒绝' },
          { value: 148, name: '已录用' }
        ]
      }
    ]
  }
  pieChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  userChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initUserChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  userChart?.dispose()
  pieChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .data-card {
    .card-content {
      display: flex;
      align-items: center;
      padding: 10px;
      
      .icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        
        :deep(.el-icon) {
          font-size: 24px;
          color: white;
        }
        
        &.blue {
          background-color: #409EFF;
        }
        
        &.green {
          background-color: #67C23A;
        }
        
        &.orange {
          background-color: #E6A23C;
        }
        
        &.purple {
          background-color: #909399;
        }
      }
      
      .data-info {
        .value {
          font-size: 24px;
          font-weight: bold;
          line-height: 1;
          margin-bottom: 8px;
        }
        
        .label {
          color: #909399;
          font-size: 14px;
        }
      }
    }
    
    .card-footer {
      border-top: 1px solid #EBEEF5;
      padding: 10px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      color: #909399;
      font-size: 13px;
      
      .change {
        &.up {
          color: #67C23A;
        }
        
        &.down {
          color: #F56C6C;
        }
      }
    }
  }
  
  .chart-row {
    margin-top: 20px;
  }
  
  .chart-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .chart-container {
      height: 300px;
      
      .chart {
        width: 100%;
        height: 100%;
      }
    }
  }
  
  .activity-card {
    margin-top: 20px;
  }
}
</style> 