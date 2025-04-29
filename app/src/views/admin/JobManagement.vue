<template>
  <div class="job-management">
    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="职位名称">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入职位名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input
            v-model="searchForm.company"
            placeholder="请输入公司名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" value="published" />
            <el-option label="未发布" value="draft" />
            <el-option label="已下线" value="offline" />
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

    <!-- 职位列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>职位列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              发布职位
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="jobList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="职位名称" min-width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              {{ row.title }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="company" label="公司名称" min-width="150" />
        <el-table-column prop="department" label="所属部门" width="120" />
        <el-table-column prop="salary" label="薪资范围" width="120" />
        <el-table-column prop="location" label="工作地点" width="120" />
        <el-table-column prop="experience" label="经验要求" width="120" />
        <el-table-column prop="education" label="学历要求" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applications" label="投递数" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewApplications(row)">
              {{ row.applications }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                :type="row.status === 'published' ? 'danger' : 'success'"
                link
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 'published' ? '下线' : '发布' }}
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDelete(row)"
              >
                删除
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

    <!-- 职位表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布职位' : '编辑职位'"
      width="800px"
    >
      <el-form
        ref="jobFormRef"
        :model="jobForm"
        :rules="jobFormRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位名称" prop="title">
              <el-input v-model="jobForm.title" placeholder="请输入职位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司名称" prop="company">
              <el-input v-model="jobForm.company" placeholder="请输入公司名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-input v-model="jobForm.department" placeholder="请输入所属部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点" prop="location">
              <el-cascader
                v-model="jobForm.location"
                :options="locationOptions"
                placeholder="请选择工作地点"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="薪资范围" prop="salary">
              <el-select v-model="jobForm.salary" placeholder="请选择薪资范围">
                <el-option label="10-15K" value="10-15K" />
                <el-option label="15-20K" value="15-20K" />
                <el-option label="20-25K" value="20-25K" />
                <el-option label="25-35K" value="25-35K" />
                <el-option label="35K以上" value="35K+" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作性质" prop="type">
              <el-select v-model="jobForm.type" placeholder="请选择工作性质">
                <el-option label="全职" value="fulltime" />
                <el-option label="兼职" value="parttime" />
                <el-option label="实习" value="intern" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经验要求" prop="experience">
              <el-select v-model="jobForm.experience" placeholder="请选择经验要求">
                <el-option label="应届生" value="fresh" />
                <el-option label="1-3年" value="1-3" />
                <el-option label="3-5年" value="3-5" />
                <el-option label="5-10年" value="5-10" />
                <el-option label="10年以上" value="10+" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历要求" prop="education">
              <el-select v-model="jobForm.education" placeholder="请选择学历要求">
                <el-option label="大专" value="college" />
                <el-option label="本科" value="bachelor" />
                <el-option label="硕士" value="master" />
                <el-option label="博士" value="phd" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="职位标签" prop="tags">
          <el-select
            v-model="jobForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入职位标签"
          >
            <el-option
              v-for="tag in tagOptions"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="职位描述" prop="description">
          <el-input
            v-model="jobForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入职位描述"
          />
        </el-form-item>

        <el-form-item label="任职要求" prop="requirements">
          <el-input
            v-model="jobForm.requirements"
            type="textarea"
            :rows="4"
            placeholder="请输入任职要求"
          />
        </el-form-item>

        <el-form-item label="发布状态" prop="status">
          <el-radio-group v-model="jobForm.status">
            <el-radio label="draft">暂存草稿</el-radio>
            <el-radio label="published">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

interface JobForm {
  title: string
  company: string
  department: string
  location: string[]
  salary: string
  type: string
  experience: string
  education: string
  tags: string[]
  description: string
  requirements: string
  status: 'draft' | 'published' | 'offline'
}

interface Job extends JobForm {
  id: number
  publishTime: string
  applications: number
}

// 搜索表单
const searchForm = reactive({
  title: '',
  company: '',
  status: ''
})

// 职位列表数据
const loading = ref(false)
const jobList: Ref<Job[]> = ref([
  {
    id: 1,
    title: '高级前端开发工程师',
    company: 'XX科技有限公司',
    department: '技术部',
    location: ['广东省', '深圳市', '南山区'],
    salary: '25-35K',
    type: 'fulltime',
    experience: '3-5',
    education: 'bachelor',
    tags: ['Vue', 'React', 'TypeScript'],
    description: '负责公司核心产品的前端开发工作',
    requirements: '1. 精通Vue、React等主流框架\n2. 具备良好的代码风格和架构能力',
    status: 'published',
    publishTime: '2024-03-20 10:00:00',
    applications: 5
  }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const jobFormRef = ref<FormInstance>()
const jobForm = reactive<JobForm>({
  title: '',
  company: '',
  department: '',
  location: [],
  salary: '',
  type: 'fulltime',
  experience: '',
  education: '',
  tags: [],
  description: '',
  requirements: '',
  status: 'draft'
})

// 表单验证规则
const jobFormRules: FormRules = {
  title: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  company: [
    { required: true, message: '请输入公司名称', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入所属部门', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请选择工作地点', trigger: 'change' }
  ],
  salary: [
    { required: true, message: '请选择薪资范围', trigger: 'change' }
  ],
  experience: [
    { required: true, message: '请选择经验要求', trigger: 'change' }
  ],
  education: [
    { required: true, message: '请选择学历要求', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入职位描述', trigger: 'blur' }
  ],
  requirements: [
    { required: true, message: '请输入任职要求', trigger: 'blur' }
  ]
}

// 地区选项（示例数据）
const locationOptions = [
  {
    value: '广东省',
    label: '广东省',
    children: [
      {
        value: '深圳市',
        label: '深圳市',
        children: [
          { value: '南山区', label: '南山区' },
          { value: '福田区', label: '福田区' }
        ]
      }
    ]
  }
]

// 标签选项
const tagOptions = [
  'Vue',
  'React',
  'Angular',
  'TypeScript',
  'Node.js',
  'Java',
  'Python',
  'Go',
  '微服务',
  '云原生'
]

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    published: 'success',
    draft: 'info',
    offline: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    published: '已发布',
    draft: '未发布',
    offline: '已下线'
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
  searchForm.title = ''
  searchForm.company = ''
  searchForm.status = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchJobList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchJobList()
}

// 获取职位列表
const fetchJobList = () => {
  loading.value = true
  // TODO: 实现获取职位列表的接口调用
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 新增职位
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(jobForm, {
    title: '',
    company: '',
    department: '',
    location: [],
    salary: '',
    type: 'fulltime',
    experience: '',
    education: '',
    tags: [],
    description: '',
    requirements: '',
    status: 'draft'
  })
}

// 编辑职位
const handleEdit = (row: Job) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(jobForm, row)
}

// 提交表单
const handleSubmit = async () => {
  if (!jobFormRef.value) return
  
  await jobFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 实现提交逻辑
      console.log('Submit form:', jobForm)
      dialogVisible.value = false
      ElMessage.success(dialogType.value === 'add' ? '发布成功' : '修改成功')
      fetchJobList()
    }
  })
}

// 切换职位状态
const handleToggleStatus = (row: Job) => {
  const action = row.status === 'published' ? '下线' : '发布'
  const newStatus = row.status === 'published' ? 'offline' : 'published'
  
  ElMessageBox.confirm(
    `确定要${action}该职位吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现状态切换的接口调用
    row.status = newStatus
    ElMessage.success(`${action}成功`)
  })
}

// 删除职位
const handleDelete = (row: Job) => {
  ElMessageBox.confirm(
    `确定要删除该职位吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现删除职位的接口调用
    ElMessage.success('删除成功')
    fetchJobList()
  })
}

// 查看投递记录
const viewApplications = (row: Job) => {
  router.push({
    name: 'ApplicationManagement',
    query: { jobId: row.id }
  })
}

// 初始化
fetchJobList()
</script>

<style lang="scss" scoped>
.job-management {
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
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

:deep(.el-button-group) {
  .el-button {
    margin-left: 0 !important;
  }
}

:deep(.el-form-item__content) {
  .el-select,
  .el-cascader {
    width: 100%;
  }
}
</style> 