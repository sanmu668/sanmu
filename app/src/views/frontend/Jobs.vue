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
        <div v-for="(job, index) in jobList" :key="job.id || index" class="job-item">
          <div class="job-info">
            <div class="title-row">
              <h3 class="job-title" @click="viewJobDetail(job)">{{ job.jobName }}</h3>
              <div class="salary">{{ job.salaryRange }}</div>
            </div>
            <div class="company-row">
              <span class="company-name">{{ job.company }}</span>
              <el-tag size="small" type="info">{{ job.location }}</el-tag>
              <el-tag size="small" type="info">{{ job.experience }}</el-tag>
              <el-tag size="small" type="info">{{ job.education }}</el-tag>
            </div>
            <div class="tags-row">
              <el-tag
                v-for="tag in job.tags.split(',')"
                :key="tag"
                size="small"
                class="job-tag"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="welfare-row" v-if="job.jobType">
              <span class="welfare-item">{{ job.jobType }}</span>
              <span class="welfare-item">{{ job.department }}</span>
            </div>
          </div>
          <div class="action-area">
            <el-button type="primary" @click="showResumeSelect(job)">立即投递</el-button>
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
      :destroy-on-close="false"
    >
      <div v-if="currentJob" class="job-detail">
        <h2>{{ currentJob.jobName }}</h2>
        <div class="salary-tag">{{ currentJob.salaryRange }}</div>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公司名称">{{ currentJob.company }}</el-descriptions-item>
          <el-descriptions-item label="工作地点">{{ currentJob.location }}</el-descriptions-item>
          <el-descriptions-item label="工作经验">{{ currentJob.experience }}</el-descriptions-item>
          <el-descriptions-item label="学历要求">{{ currentJob.education }}</el-descriptions-item>
          <el-descriptions-item label="工作类型">{{ currentJob.jobType }}</el-descriptions-item>
          <el-descriptions-item label="所属部门">{{ currentJob.department }}</el-descriptions-item>
        </el-descriptions>

        <div class="section">
          <h3>职位描述</h3>
          <div class="content" v-html="currentJob.description"></div>
        </div>

        <div class="section">
          <h3>职位要求</h3>
          <div class="content" v-html="currentJob.requirements"></div>
        </div>

        <div class="section">
          <h3>职位标签</h3>
          <div class="welfare-tags">
            <el-tag
              v-for="tag in currentJob.tags.split(',')"
              :key="tag"
              class="welfare-tag"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="jobDetailVisible = false">关闭</el-button>
          <el-button type="primary" @click="currentJob && showResumeSelect(currentJob)">
            立即投递
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 简历选择对话框 -->
    <el-dialog
      v-model="resumeSelectVisible"
      title="选择简历"
      width="60%"
      :destroy-on-close="false"
      @close="handleDialogClose"
    >
      <div v-loading="resumeLoading">
        <div v-if="resumeList.length > 0" class="resume-list">
          <el-radio-group v-model="selectedResumeId">
            <el-radio
              v-for="resume in resumeList"
              :key="resume.id"
              :label="resume.id"
              class="resume-radio"
            >
              <div class="resume-item">
                <div class="resume-title">{{ resume.title }}</div>
                <div class="resume-date">{{ formatDate(resume.updateTime) }}</div>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
        <div v-else class="empty-tip">
          <el-empty description="暂无简历，请先创建简历" />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click.prevent.stop="handleApplyJob">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

interface Job {
  id: number
  jobName: string
  department: string
  salaryRange: string
  experience: string
  education: string
  jobType: string
  description: string
  requirements: string
  company: string
  location: string
  tags: string
  status: string
}

interface ApiResponse {
  content: Job[]
  totalElements: number
  totalPages: number
}

// 定义本地存储的key
const CURRENT_JOB_KEY = 'current_job_info'

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
const total = ref(0)
const jobList = ref<Job[]>([])

// 职位和简历相关状态
const jobDetailVisible = ref(false)
const currentJob = ref<Job | null>(null)
const resumeSelectVisible = ref(false)
const resumeList = ref<Array<{id: number, title: string, updateTime: string}>>([])
const selectedResumeId = ref<number | ''>('')
const resumeLoading = ref(false)

// 监听 currentJob 变化，保存到 localStorage
watch(currentJob, (newVal) => {
  if (newVal) {
    console.log('currentJob 发生变化，保存到 localStorage:', newVal)
    localStorage.setItem(CURRENT_JOB_KEY, JSON.stringify(newVal))
  }
}, { deep: true })

// 从 localStorage 恢复职位信息
const restoreJobInfo = () => {
  console.group('restoreJobInfo')
  try {
    const savedJob = localStorage.getItem(CURRENT_JOB_KEY)
    console.log('从 localStorage 读取的数据：', savedJob)
    
    if (savedJob) {
      currentJob.value = JSON.parse(savedJob)
      console.log('恢复的职位信息：', currentJob.value)
    } else {
      console.log('没有找到保存的职位信息')
    }
  } catch (error) {
    console.error('恢复职位信息失败：', error)
    localStorage.removeItem(CURRENT_JOB_KEY)
  } finally {
    console.groupEnd()
  }
}

// 获取职位列表
const fetchJobs = async () => {
  loading.value = true
  try {
    console.log('开始获取职位列表，参数:', {
      jobName: searchForm.keyword,
      location: searchForm.city,
      experience: searchForm.experience,
      page: currentPage.value,
      size: pageSize.value
    })

    const response = await axios.get<ApiResponse>('http://localhost:8080/api/user/jobs', {
      params: {
        jobName: searchForm.keyword,
        location: searchForm.city,
        experience: searchForm.experience,
        page: currentPage.value,
        size: pageSize.value
      }
    })

    console.log('获取到的原始职位数据:', response.data)

    // 验证并处理返回的数据
    if (response.data && Array.isArray(response.data.content)) {
      jobList.value = response.data.content.map(job => {
        // 打印每个职位的原始数据
        console.log('处理职位数据:', job)
        
        // 确保返回一个有效的职位对象
        const processedJob = {
          id: Number(job.id) || 0, // 确保 id 是数字类型
          jobName: job.jobName || '未知职位',
          department: job.department || '未知部门',
          salaryRange: job.salaryRange || '面议',
          experience: job.experience || '无要求',
          education: job.education || '无要求',
          jobType: job.jobType || '全职',
          description: job.description || '',
          requirements: job.requirements || '',
          company: job.company || '未知公司',
          location: job.location || '未知地点',
          tags: typeof job.tags === 'string' ? job.tags : '',
          status: job.status || '在招'
        }

        // 验证处理后的数据
        if (!processedJob.id) {
          console.warn('警告：职位ID无效:', processedJob)
        }

        return processedJob
      })

      console.log('处理后的职位列表:', jobList.value)
    } else {
      console.error('获取到的职位数据格式不正确:', response.data)
      jobList.value = []
    }
    
    total.value = response.data.totalElements || 0
  } catch (error) {
    console.error('获取职位列表失败:', error)
    ElMessage.error('获取职位列表失败')
    jobList.value = []
  } finally {
    loading.value = false
  }
}

// 搜索方法
const handleSearch = () => {
  currentPage.value = 1
  fetchJobs()
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
  fetchJobs()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchJobs()
}

// 获取简历列表
const fetchResumeList = async () => {
  console.group('fetchResumeList')
  console.log('开始获取简历列表')
  resumeLoading.value = true
  
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录后再投递简历')
      throw new Error('未登录状态')
    }
    
    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const response = await axios.get(`${baseURL}/api/user/resumes`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept': '*/*'
      },
      timeout: 10000 // 10 seconds timeout
    })
    
    console.log('简历列表响应:', response)
    
    if (response.data && response.status === 200) {
      // 确保返回的数据是数组
      resumeList.value = Array.isArray(response.data) ? response.data : 
                        Array.isArray(response.data.content) ? response.data.content : 
                        [response.data]
      console.log('处理后的简历列表:', resumeList.value)
      
      if (resumeList.value.length === 0) {
        ElMessage.warning('您还没有创建简历，请先创建简历')
      }
    } else {
      console.error('响应数据格式不正确:', response)
      throw new Error('获取简历列表失败：响应数据格式不正确')
    }
  } catch (error: any) {
    console.error('获取简历列表失败:', error)
    resumeList.value = []
    
    if (error.response) {
      // 服务器响应错误
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          // 可以在这里添加重定向到登录页面的逻辑
          break
        case 403:
          ElMessage.error('没有权限访问简历列表')
          break
        case 404:
          ElMessage.error('找不到简历列表资源')
          break
        default:
          ElMessage.error(`获取简历列表失败: ${error.response.data?.message || '服务器错误'}`)
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      // 请求配置出错
      ElMessage.error(`获取简历列表失败: ${error.message || '未知错误'}`)
    }
    throw error
  } finally {
    resumeLoading.value = false
    console.groupEnd()
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 修改投递简历方法
const showResumeSelect = async (job: Job) => {
  console.group('showResumeSelect')
  console.log('开始选择简历流程，原始职位数据：', job)
  
  try {
    // 检查登录状态
    const token = localStorage.getItem('token')
    if (!token) {
      console.log('未登录状态')
      ElMessage.warning('请先登录后再投递简历')
      return
    }

    // 详细验证职位信息
    if (!job) {
      throw new Error('职位对象为空')
    }

    if (!job.id || typeof job.id !== 'number') {
      console.error('职位ID类型错误:', typeof job.id, job.id)
      throw new Error('职位ID无效')
    }

    // 创建一个新的职位对象，确保所有字段都存在且类型正确
    const jobData = {
      id: Number(job.id),
      jobName: job.jobName || '未知职位',
      department: job.department || '未知部门',
      salaryRange: job.salaryRange || '面议',
      experience: job.experience || '无要求',
      education: job.education || '无要求',
      jobType: job.jobType || '全职',
      description: job.description || '',
      requirements: job.requirements || '',
      company: job.company || '未知公司',
      location: job.location || '未知地点',
      tags: typeof job.tags === 'string' ? job.tags : '',
      status: job.status || '在招'
    }

    // 保存职位信息
    currentJob.value = jobData
    console.log('已保存的职位信息：', currentJob.value)

    // 保存到 localStorage
    try {
      localStorage.setItem(CURRENT_JOB_KEY, JSON.stringify(jobData))
      console.log('职位信息已保存到 localStorage')
    } catch (e) {
      console.error('保存职位信息到localStorage失败:', e)
      // 这个错误不影响主流程，可以继续
    }
    
    // 获取简历列表
    resumeLoading.value = true
    try {
      await fetchResumeList()
      
      if (resumeList.value.length > 0) {
        resumeSelectVisible.value = true
        selectedResumeId.value = '' // 重置选中的简历ID
      } else {
        ElMessage.warning('您还没有创建简历，请先创建简历')
      }
    } catch (error: any) {
      console.error('获取简历列表失败：', error)
      if (error.response?.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(`获取简历列表失败: ${error.response?.data?.message || error.message || '未知错误'}`)
      }
    }
    
  } catch (error: any) {
    console.error('showResumeSelect 出错：', error)
    ElMessage.error(error.message || '选择简历时出错，请重试')
  } finally {
    resumeLoading.value = false
    console.groupEnd()
  }
}

// 修改 handleApplyJob 方法
const handleApplyJob = async () => {
  console.group('handleApplyJob')
  console.log('开始投递流程')
  
  try {
    // 验证必要信息
    if (!selectedResumeId.value) {
      ElMessage.warning('请选择要投递的简历')
      console.log('未选择简历')
      return
    }

    if (!currentJob.value?.id) {
      ElMessage.error('职位信息无效，请重新选择职位')
      resumeSelectVisible.value = false
      return
    }

    // 所有验证通过，开始投递
    console.log('开始调用 applyJob')
    await applyJob()
    
  } catch (error) {
    console.error('handleApplyJob 出错：', error)
    ElMessage.error('投递失败，请重试')
  } finally {
    console.groupEnd()
  }
}

// 修改 applyJob 方法
const applyJob = async () => {
  console.group('applyJob')
  console.log('开始发送投递请求')
  
  try {
    // 再次验证数据
    if (!currentJob.value?.id) {
      throw new Error('职位信息无效')
    }

    const token = localStorage.getItem('token')
    if (!token) {
      throw new Error('未登录状态')
    }

    const requestData = {
      jobId: currentJob.value.id,
      resumeId: selectedResumeId.value
    }
    
    console.log('请求数据：', requestData)
    
    const response = await axios.post(
      'http://localhost:8080/api/applications/sumit', 
      requestData,
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      }
    )

    console.log('投递响应：', response)

    if (response.status >= 200 && response.status < 300) {
      ElMessage.success(`简历已成功投递到"${currentJob.value.jobName}"职位`)
      resumeSelectVisible.value = false
      selectedResumeId.value = ''
      // 清除保存的职位信息
      localStorage.removeItem(CURRENT_JOB_KEY)
      console.log('投递成功，已清除本地存储')
    } else {
      throw new Error('服务器响应异常')
    }
  } catch (error: any) {
    console.error('投递请求失败：', error)
    if (error.response?.data) {
      console.error('服务器错误信息：', error.response.data)
      ElMessage.error(error.response.data.message || '投递失败，请重试')
    } else {
      ElMessage.error(`投递失败: ${error.message || '请重试'}`)
    }
    throw error
  } finally {
    console.groupEnd()
  }
}

const viewJobDetail = (job: Job) => {
  console.group('viewJobDetail')
  console.log('查看职位详情，原始数据:', job)
  
  try {
    if (!job) {
      throw new Error('职位对象为空')
    }

    if (!job.id || typeof job.id !== 'number') {
      console.error('职位ID类型错误:', typeof job.id, job.id)
      throw new Error('职位ID无效')
    }

    // 创建一个新的对象，确保所有必要字段都存在且类型正确
    currentJob.value = {
      id: Number(job.id),
      jobName: job.jobName || '未知职位',
      department: job.department || '未知部门',
      salaryRange: job.salaryRange || '面议',
      experience: job.experience || '无要求',
      education: job.education || '无要求',
      jobType: job.jobType || '全职',
      description: job.description || '',
      requirements: job.requirements || '',
      company: job.company || '未知公司',
      location: job.location || '未知地点',
      tags: typeof job.tags === 'string' ? job.tags : '',
      status: job.status || '在招'
    }

    console.log('设置当前职位信息:', currentJob.value)
    jobDetailVisible.value = true
  } catch (error: any) {
    console.error('查看职位详情失败:', error)
    ElMessage.error(error.message || '职位信息无效')
  } finally {
    console.groupEnd()
  }
}

// 添加测试函数
const testLog = () => {
  console.log('测试日志按钮被点击')
  alert('测试日志按钮被点击')
}

// 组件挂载时
onMounted(() => {
  console.group('组件挂载')
  console.log('开始加载职位列表')
  try {
    restoreJobInfo()
    console.log('恢复的职位信息：', currentJob.value)
  } catch (error) {
    console.error('恢复职位信息失败：', error)
  }
  fetchJobs()
  console.groupEnd()
})

// 组件卸载前
onBeforeUnmount(() => {
  console.group('组件卸载')
  console.log('清理本地存储')
  localStorage.removeItem(CURRENT_JOB_KEY)
  console.groupEnd()
})

// 添加监听器，当对话框打开时重新加载简历列表
watch(resumeSelectVisible, async (newValue) => {
  if (newValue) {
    console.log('简历选择对话框被打开，重新加载简历列表')
    await fetchResumeList()
  }
})

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    console.log('发送请求:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data
    })
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 添加响应拦截器
axios.interceptors.response.use(
  response => {
    console.log('收到响应:', {
      status: response.status,
      headers: response.headers,
      data: response.data
    })
    return response
  },
  error => {
    console.error('响应错误:', error)
    return Promise.reject(error)
  }
)

// 处理对话框关闭
const handleDialogClose = () => {
  console.log('对话框关闭')
  selectedResumeId.value = ''
  currentJob.value = null
  localStorage.removeItem(CURRENT_JOB_KEY)
}

// 处理简历选择
const handleResumeSelect = (value: number) => {
  console.log('选择的简历ID:', value)
  selectedResumeId.value = value
}

// 处理取消按钮
const handleCancel = () => {
  console.log('取消投递')
  resumeSelectVisible.value = false
  selectedResumeId.value = ''
  currentJob.value = null
  localStorage.removeItem(CURRENT_JOB_KEY)
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

.resume-list {
  padding: 20px;
  
  .resume-radio {
    display: block;
    margin-bottom: 15px;
    padding: 15px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 4px;
    transition: all 0.3s;
    
    &:hover {
      background-color: var(--el-fill-color-light);
    }
    
    &.is-checked {
      border-color: var(--el-color-primary);
    }
  }
  
  .resume-item {
    .resume-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }
    
    .resume-date {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin-top: 5px;
    }
  }
}

.empty-tip {
  padding: 40px 0;
  text-align: center;
}
</style> 