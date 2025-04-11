<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>对话总数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.totalChats || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>简历分析数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.totalResumes || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>推荐职位数</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ stats.totalJobs || 0 }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>对话趋势</span>
            </div>
          </template>
          <div class="chart-container" ref="chatChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>简历分析趋势</span>
            </div>
          </template>
          <div class="chart-container" ref="resumeChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const stats = ref({
  totalChats: 0,
  totalResumes: 0,
  totalJobs: 0
})

const chatChartRef = ref(null)
const resumeChartRef = ref(null)
let chatChart = null
let resumeChart = null

const fetchStats = async () => {
  try {
    const response = await axios.get('/api/dashboard/stats')
    stats.value = response.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const initCharts = () => {
  chatChart = echarts.init(chatChartRef.value)
  resumeChart = echarts.init(resumeChartRef.value)

  const chatOption = {
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
    series: [{
      data: [0, 0, 0, 0, 0, 0, 0],
      type: 'line',
      smooth: true
    }]
  }

  const resumeOption = {
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
    series: [{
      data: [0, 0, 0, 0, 0, 0, 0],
      type: 'line',
      smooth: true
    }]
  }

  chatChart.setOption(chatOption)
  resumeChart.setOption(resumeOption)
}

onMounted(() => {
  fetchStats()
  initCharts()

  window.addEventListener('resize', () => {
    chatChart?.resize()
    resumeChart?.resize()
  })
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;

  .el-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-content {
    text-align: center;
    padding: 20px 0;

    h2 {
      margin: 0;
      font-size: 28px;
      color: #409EFF;
    }
  }

  .chart-container {
    height: 300px;
  }

  .mt-4 {
    margin-top: 20px;
  }
}
</style>