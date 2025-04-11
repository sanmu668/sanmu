<template>
  <div class="jobs-container">
    <el-card class="jobs-card">
      <template #header>
        <div class="card-header">
          <span>职位推荐</span>
          <el-button type="primary" @click="handleRefresh">刷新推荐</el-button>
        </div>
      </template>

      <el-table :data="jobs" v-loading="loading">
        <el-table-column prop="title" label="职位名称" />
        <el-table-column prop="company" label="公司名称" />
        <el-table-column prop="location" label="工作地点" width="120" />
        <el-table-column prop="salary" label="薪资范围" width="120" />
        <el-table-column prop="matchRate" label="匹配度" width="120">
          <template #default="{ row }">
            <el-progress
              :percentage="row.matchRate"
              :format="format"
              :status="row.matchRate >= 80 ? 'success' : ''"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="detailVisible"
      title="职位详情"
      width="60%"
    >
      <div class="job-detail" v-if="currentJob">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="职位名称">
            {{ currentJob.title }}
          </el-descriptions-item>
          <el-descriptions-item label="公司名称">
            {{ currentJob.company }}
          </el-descriptions-item>
          <el-descriptions-item label="工作地点">
            {{ currentJob.location }}
          </el-descriptions-item>
          <el-descriptions-item label="薪资范围">
            {{ currentJob.salary }}
          </el-descriptions-item>
          <el-descriptions-item label="职位描述">
            <div class="description">
              {{ currentJob.description }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="技能要求">
            <el-tag
              v-for="(skill, index) in currentJob.skills"
              :key="index"
              class="skill-tag"
            >
              {{ skill }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const jobs = ref([])
const detailVisible = ref(false)
const currentJob = ref(null)

const format = (percentage) => percentage + '%'

const fetchJobs = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/jobs/recommendations')
    jobs.value = response.data
  } catch (error) {
    console.error('获取职位推荐失败:', error)
    ElMessage.error('获取职位推荐失败')
  } finally {
    loading.value = false
  }
}

const handleRefresh = () => {
  fetchJobs()
}

const handleView = (job) => {
  currentJob.value = job
  detailVisible.value = true
}

fetchJobs()
</script>

<style lang="scss" scoped>
.jobs-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .job-detail {
    .description {
      white-space: pre-wrap;
      line-height: 1.5;
    }

    .skill-tag {
      margin-right: 8px;
      margin-bottom: 8px;
    }
  }
}
</style>