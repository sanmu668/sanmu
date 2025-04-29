<template>
  <div class="jobs-container">
    <!-- 搜索栏 -->
    <el-card class="search-card mb-20">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="职位名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入职位名称" />
        </el-form-item>
        <el-form-item label="工作城市">
          <el-select v-model="searchForm.city" placeholder="请选择城市">
            <el-option label="全部" value="" />
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="广州" value="广州" />
            <el-option label="深圳" value="深圳" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作经验">
          <el-select v-model="searchForm.experience" placeholder="请选择工作经验">
            <el-option label="全部" value="" />
            <el-option label="应届生" value="应届生" />
            <el-option label="1-3年" value="1-3年" />
            <el-option label="3-5年" value="3-5年" />
            <el-option label="5年以上" value="5年以上" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 职位列表 -->
    <el-card class="job-list">
      <template #header>
        <div class="card-header">
          <span>推荐职位</span>
          <div class="right">
            <el-radio-group v-model="sortBy" size="small">
              <el-radio-button label="recommend">推荐优先</el-radio-button>
              <el-radio-button label="latest">最新发布</el-radio-button>
              <el-radio-button label="salary">薪资最高</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div v-loading="loading">
        <div v-for="job in jobList" :key="job.id" class="job-item">
          <div class="job-info">
            <div class="title-row">
              <h3 class="job-title" @click="viewJobDetail(job)">{{ job.title }}</h3>
              <div class="salary">{{ job.salary }}</div>
            </div>
            <div class="company-row">
              <span class="company-name">{{ job.company }}</span>
              <el-tag size="small" type="info">{{ job.city }}</el-tag>
              <el-tag size="small" type="info">{{ job.experience }}</el-tag>
              <el-tag size="small" type="info">{{ job.education }}</el-tag>
            </div>
            <div class="tags-row">
              <el-tag
                v-for="tag in job.tags"
                :key="tag"
                size="small"
                class="job-tag"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="welfare-row">
              <span v-for="welfare in job.welfare" :key="welfare" class="welfare-item">
                {{ welfare }}
              </span>
            </div>
          </div>
          <div class="action-area">
            <el-button type="primary" @click="applyJob(job)">立即投递</el-button>
          </div>
        </div>

        <!-- 分页 -->
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
      </div>
    </el-card>

    <!-- 职位详情对话框 -->
    <el-dialog
      v-model="jobDetailVisible"
      title="职位详情"
      width="60%"
      destroy-on-close
    >
      <div v-if="currentJob" class="job-detail">
        <h2>{{ currentJob.title }}</h2>
        <div class="salary-tag">{{ currentJob.salary }}</div>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公司名称">{{ currentJob.company }}</el-descriptions-item>
          <el-descriptions-item label="工作地点">{{ currentJob.city }}</el-descriptions-item>
          <el-descriptions-item label="工作经验">{{ currentJob.experience }}</el-descriptions-item>
          <el-descriptions-item label="学历要求">{{ currentJob.education }}</el-descriptions-item>
        </el-descriptions>

        <div class="section">
          <h3>职位描述</h3>
          <div class="content" v-html="currentJob.description"></div>
        </div>

        <div class="section">
          <h3>职位要求</h3>
          <div class="content" v-html="currentJob.requirement"></div>
        </div>

        <div class="section">
          <h3>公司福利</h3>
          <div class="welfare-tags">
            <el-tag
              v-for="welfare in currentJob.welfare"
              :key="welfare"
              class="welfare-tag"
            >
              {{ welfare }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="jobDetailVisible = false">关闭</el-button>
          <el-button type="primary" @click="applyJob(currentJob)">
            立即投递
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

interface Job {
  id: number
  title: string
  company: string
  salary: string
  city: string
  experience: string
  education: string
  tags: string[]
  welfare: string[]
  description: string
  requirement: string
}

// 搜索表单
const searchForm = reactive({
  keyword: '',
  city: '',
  experience: ''
})

// 列表数据
const loading = ref(false)
const sortBy = ref('recommend')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 模拟职位数据
const jobList = ref<Job[]>([
  {
    id: 1,
    title: '高级前端开发工程师',
    company: '智简科技有限公司',
    salary: '25k-35k',
    city: '北京',
    experience: '3-5年',
    education: '本科',
    tags: ['Vue', 'React', 'TypeScript'],
    welfare: ['五险一金', '年终奖', '加班补助', '免费零食'],
    description: '<p>1. 负责公司前端项目的开发和维护</p><p>2. 参与技术方案设计和架构优化</p>',
    requirement: '<p>1. 精通Vue/React等主流框架</p><p>2. 有良好的代码风格和架构意识</p>'
  },
  {
    id: 2,
    title: '后端开发工程师',
    company: '未来科技有限公司',
    salary: '20k-30k',
    city: '上海',
    experience: '1-3年',
    education: '本科',
    tags: ['Java', 'Spring Boot', 'MySQL'],
    welfare: ['五险一金', '年终奖', '定期体检', '团建活动'],
    description: '<p>1. 负责后端服务开发</p><p>2. 参与数据库设计</p>',
    requirement: '<p>1. 熟悉Java开发</p><p>2. 了解常用数据库</p>'
  }
])

// 职位详情
const jobDetailVisible = ref(false)
const currentJob = ref<Job | null>(null)

// 搜索方法
const handleSearch = () => {
  loading.value = true
  console.log('搜索条件:', searchForm)
  // 模拟接口调用
  setTimeout(() => {
    loading.value = false
    ElMessage.success('搜索成功')
  }, 1000)
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.city = ''
  searchForm.experience = ''
  handleSearch()
}

// 分页方法
const handleSizeChange = (val: number) => {
  pageSize.value = val
  handleSearch()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  handleSearch()
}

// 查看职位详情
const viewJobDetail = (job: Job) => {
  currentJob.value = job
  jobDetailVisible.value = true
}

// 投递简历
const applyJob = (job: Job | null) => {
  if (!job) return
  ElMessage.success(`简历已投递到"${job.title}"职位`)
  jobDetailVisible.value = false
}
</script>

<style lang="scss" scoped>
.jobs-container {
  .job-list {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .job-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 20px;
      border-bottom: 1px solid var(--el-border-color-lighter);
      
      &:last-child {
        border-bottom: none;
      }
      
      .job-info {
        flex: 1;
        margin-right: 20px;
        
        .title-row {
          display: flex;
          align-items: center;
          margin-bottom: 10px;
          
          .job-title {
            margin: 0;
            font-size: 18px;
            color: var(--el-color-primary);
            cursor: pointer;
            
            &:hover {
              color: var(--el-color-primary-light-3);
            }
          }
          
          .salary {
            margin-left: 20px;
            color: #f56c6c;
            font-size: 16px;
            font-weight: bold;
          }
        }
        
        .company-row {
          margin-bottom: 10px;
          
          .company-name {
            margin-right: 10px;
            color: #606266;
          }
          
          .el-tag {
            margin-right: 10px;
          }
        }
        
        .tags-row {
          margin-bottom: 10px;
          
          .job-tag {
            margin-right: 8px;
            margin-bottom: 8px;
          }
        }
        
        .welfare-row {
          color: #909399;
          font-size: 14px;
          
          .welfare-item {
            margin-right: 10px;
            
            &:not(:last-child)::after {
              content: '|';
              margin-left: 10px;
              color: #dcdfe6;
            }
          }
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.job-detail {
  .salary-tag {
    color: #f56c6c;
    font-size: 20px;
    font-weight: bold;
    margin: 10px 0 20px;
  }
  
  .section {
    margin-top: 20px;
    
    h3 {
      margin-bottom: 10px;
      font-size: 16px;
      color: #303133;
    }
    
    .content {
      color: #606266;
      line-height: 1.6;
    }
    
    .welfare-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }
  }
}
</style> 