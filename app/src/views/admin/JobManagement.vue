<template>
  <div class="job-management">
    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="职位名称">
          <el-input
            v-model="searchForm.jobName"
            placeholder="请输入职位名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="公司名称">
          <el-input
            v-model="searchForm.company"
            placeholder="请输入公司名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="已发布" value="1" />
            <el-option label="未发布" value="0" />
            <el-option label="已下线" value="2" />
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
        <el-table-column prop="jobName" label="职位名称" min-width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              {{ row.jobName }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="company" label="公司名称" min-width="150" />
        <el-table-column prop="department" label="所属部门" width="120" />
        <el-table-column prop="salaryRange" label="薪资范围" width="120" />
        <el-table-column prop="location" label="工作地点" width="120" />
        <el-table-column prop="experience" label="经验要求" width="120" />
        <el-table-column prop="education" label="学历要求" width="100" />
        <el-table-column prop="jobType" label="工作类型" width="100" />
        <el-table-column label="职位标签" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="tag in row.tags"
              :key="tag"
              size="small"
              class="mx-1"
            >
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
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
                :type="row.status === '1' ? 'danger' : 'success'"
                link
                @click="handleToggleStatus(row)"
              >
                {{ row.status === '1' ? '下线' : '发布' }}
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
            <el-form-item label="职位名称" prop="jobName">
              <el-input v-model="jobForm.jobName" placeholder="请输入职位名称" />
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
            <el-form-item label="薪资范围" prop="salaryRange">
              <el-select v-model="jobForm.salaryRange" placeholder="请选择薪资范围">
                <el-option label="10-15K" value="10-15K" />
                <el-option label="15-20K" value="15-20K" />
                <el-option label="20-25K" value="20-25K" />
                <el-option label="25-35K" value="25-35K" />
                <el-option label="35K以上" value="35K+" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作性质" prop="jobType">
              <el-select v-model="jobForm.jobType" placeholder="请选择工作性质">
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
            <el-radio label="0">暂存草稿</el-radio>
            <el-radio label="1">立即发布</el-radio>
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
import { ref, reactive, onMounted } from 'vue'
import type { Ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getJobList, updateJobStatus, deleteJob, createJob, updateJob } from '@/api/job'
import type { Job } from '@/api/job'

const router = useRouter()

// 定义图标组件
const icons = {
  Search,
  Refresh,
  Plus
}

// 定义内部使用的职位类型
interface InternalJob extends Omit<Job, 'tags' | 'location'> {
  tags: string[] | string
  location: string[] | string
}

// 表单数据类型
interface JobForm {
  id?: number
  jobName: string
  company: string
  department: string
  location: string[]
  salaryRange: string
  jobType: string
  experience: string
  education: string
  tags: string[]
  description: string
  requirements: string
  status: string
}

// 搜索表单
const searchForm = reactive({
  jobName: '',
  company: '',
  status: ''
})

// 职位列表数据
const loading = ref(false)
const jobList = ref<InternalJob[]>([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const jobFormRef = ref<FormInstance>()
const jobForm = reactive<JobForm>({
  jobName: '',
  company: '',
  department: '',
  location: [],
  salaryRange: '',
  jobType: 'fulltime',
  experience: '',
  education: '',
  tags: [],
  description: '',
  requirements: '',
  status: '0'
})

// 表单验证规则
const jobFormRules: FormRules = {
  jobName: [
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
  salaryRange: [
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
    value: '全国',
    label: '全国'
  },
  {
    value: '北京市',
    label: '北京市',
    children: [
      {
        value: '全市',
        label: '全市'
      },
      {
        value: '朝阳区',
        label: '朝阳区'
      },
      {
        value: '海淀区',
        label: '海淀区'
      },
      {
        value: '丰台区',
        label: '丰台区'
      },
      {
        value: '西城区',
        label: '西城区'
      }
    ]
  },
  {
    value: '上海市',
    label: '上海市',
    children: [
      {
        value: '全市',
        label: '全市'
      },
      {
        value: '浦东新区',
        label: '浦东新区'
      },
      {
        value: '徐汇区',
        label: '徐汇区'
      },
      {
        value: '静安区',
        label: '静安区'
      },
      {
        value: '长宁区',
        label: '长宁区'
      }
    ]
  },
  {
    value: '广东省',
    label: '广东省',
    children: [
      {
        value: '深圳市',
        label: '深圳市',
        children: [
          { value: '南山区', label: '南山区' },
          { value: '福田区', label: '福田区' },
          { value: '罗湖区', label: '罗湖区' },
          { value: '宝安区', label: '宝安区' },
          { value: '龙岗区', label: '龙岗区' }
        ]
      },
      {
        value: '广州市',
        label: '广州市',
        children: [
          { value: '天河区', label: '天河区' },
          { value: '越秀区', label: '越秀区' },
          { value: '海珠区', label: '海珠区' },
          { value: '荔湾区', label: '荔湾区' }
        ]
      },
      {
        value: '东莞市',
        label: '东莞市'
      },
      {
        value: '珠海市',
        label: '珠海市'
      }
    ]
  },
  {
    value: '浙江省',
    label: '浙江省',
    children: [
      {
        value: '杭州市',
        label: '杭州市',
        children: [
          { value: '西湖区', label: '西湖区' },
          { value: '滨江区', label: '滨江区' },
          { value: '上城区', label: '上城区' },
          { value: '余杭区', label: '余杭区' }
        ]
      },
      {
        value: '宁波市',
        label: '宁波市',
        children: [
          { value: '海曙区', label: '海曙区' },
          { value: '江北区', label: '江北区' }
        ]
      },
      {
        value: '温州市',
        label: '温州市'
      }
    ]
  },
  {
    value: '江苏省',
    label: '江苏省',
    children: [
      {
        value: '南京市',
        label: '南京市',
        children: [
          { value: '玄武区', label: '玄武区' },
          { value: '秦淮区', label: '秦淮区' },
          { value: '建邺区', label: '建邺区' }
        ]
      },
      {
        value: '苏州市',
        label: '苏州市',
        children: [
          { value: '姑苏区', label: '姑苏区' },
          { value: '工业园区', label: '工业园区' },
          { value: '高新区', label: '高新区' }
        ]
      },
      {
        value: '无锡市',
        label: '无锡市'
      },
      {
        value: '常州市',
        label: '常州市'
      }
    ]
  },
  {
    value: '山东省',
    label: '山东省',
    children: [
      {
        value: '青岛市',
        label: '青岛市',
        children: [
          { value: '市南区', label: '市南区' },
          { value: '市北区', label: '市北区' }
        ]
      },
      {
        value: '济南市',
        label: '济南市'
      }
    ]
  },
  {
    value: '四川省',
    label: '四川省',
    children: [
      {
        value: '成都市',
        label: '成都市',
        children: [
          { value: '武侯区', label: '武侯区' },
          { value: '锦江区', label: '锦江区' },
          { value: '高新区', label: '高新区' }
        ]
      }
    ]
  },
  {
    value: '福建省',
    label: '福建省',
    children: [
      {
        value: '厦门市',
        label: '厦门市',
        children: [
          { value: '思明区', label: '思明区' },
          { value: '湖里区', label: '湖里区' }
        ]
      },
      {
        value: '福州市',
        label: '福州市'
      }
    ]
  },
  {
    value: '湖北省',
    label: '湖北省',
    children: [
      {
        value: '武汉市',
        label: '武汉市',
        children: [
          { value: '江岸区', label: '江岸区' },
          { value: '武昌区', label: '武昌区' },
          { value: '洪山区', label: '洪山区' }
        ]
      }
    ]
  },
  {
    value: '天津市',
    label: '天津市',
    children: [
      {
        value: '全市',
        label: '全市'
      },
      {
        value: '和平区',
        label: '和平区'
      },
      {
        value: '南开区',
        label: '南开区'
      }
    ]
  },
  {
    value: '重庆市',
    label: '重庆市',
    children: [
      {
        value: '全市',
        label: '全市'
      },
      {
        value: '渝中区',
        label: '渝中区'
      },
      {
        value: '江北区',
        label: '江北区'
      }
    ]
  },
  {
    value: '河南省',
    label: '河南省',
    children: [
      {
        value: '郑州市',
        label: '郑州市',
        children: [
          { value: '金水区', label: '金水区' },
          { value: '中原区', label: '中原区' }
        ]
      }
    ]
  },
  {
    value: '陕西省',
    label: '陕西省',
    children: [
      {
        value: '西安市',
        label: '西安市',
        children: [
          { value: '雁塔区', label: '雁塔区' },
          { value: '莲湖区', label: '莲湖区' }
        ]
      }
    ]
  },
  {
    value: '湖南省',
    label: '湖南省',
    children: [
      {
        value: '长沙市',
        label: '长沙市',
        children: [
          { value: '岳麓区', label: '岳麓区' },
          { value: '天心区', label: '天心区' },
          { value: '高新区', label: '高新区' }
        ]
      },
      {
        value: '株洲市',
        label: '株洲市'
      }
    ]
  },
  {
    value: '安徽省',
    label: '安徽省',
    children: [
      {
        value: '合肥市',
        label: '合肥市',
        children: [
          { value: '蜀山区', label: '蜀山区' },
          { value: '包河区', label: '包河区' },
          { value: '高新区', label: '高新区' }
        ]
      },
      {
        value: '芜湖市',
        label: '芜湖市'
      }
    ]
  },
  {
    value: '辽宁省',
    label: '辽宁省',
    children: [
      {
        value: '沈阳市',
        label: '沈阳市',
        children: [
          { value: '和平区', label: '和平区' },
          { value: '沈河区', label: '沈河区' }
        ]
      },
      {
        value: '大连市',
        label: '大连市',
        children: [
          { value: '中山区', label: '中山区' },
          { value: '西岗区', label: '西岗区' },
          { value: '高新园区', label: '高新园区' }
        ]
      }
    ]
  },
  {
    value: '河北省',
    label: '河北省',
    children: [
      {
        value: '石家庄市',
        label: '石家庄市',
        children: [
          { value: '长安区', label: '长安区' },
          { value: '桥西区', label: '桥西区' }
        ]
      },
      {
        value: '雄安新区',
        label: '雄安新区'
      }
    ]
  },
  {
    value: '江西省',
    label: '江西省',
    children: [
      {
        value: '南昌市',
        label: '南昌市',
        children: [
          { value: '东湖区', label: '东湖区' },
          { value: '西湖区', label: '西湖区' }
        ]
      }
    ]
  },
  {
    value: '海南省',
    label: '海南省',
    children: [
      {
        value: '海口市',
        label: '海口市',
        children: [
          { value: '龙华区', label: '龙华区' },
          { value: '秀英区', label: '秀英区' }
        ]
      },
      {
        value: '三亚市',
        label: '三亚市'
      },
      {
        value: '海南自贸港',
        label: '海南自贸港'
      }
    ]
  },
  {
    value: '云南省',
    label: '云南省',
    children: [
      {
        value: '昆明市',
        label: '昆明市',
        children: [
          { value: '五华区', label: '五华区' },
          { value: '官渡区', label: '官渡区' }
        ]
      }
    ]
  },
  {
    value: '贵州省',
    label: '贵州省',
    children: [
      {
        value: '贵阳市',
        label: '贵阳市',
        children: [
          { value: '南明区', label: '南明区' },
          { value: '观山湖区', label: '观山湖区' }
        ]
      }
    ]
  },
  {
    value: '广西壮族自治区',
    label: '广西壮族自治区',
    children: [
      {
        value: '南宁市',
        label: '南宁市',
        children: [
          { value: '青秀区', label: '青秀区' },
          { value: '兴宁区', label: '兴宁区' }
        ]
      },
      {
        value: '桂林市',
        label: '桂林市'
      }
    ]
  },
  {
    value: '山西省',
    label: '山西省',
    children: [
      {
        value: '太原市',
        label: '太原市',
        children: [
          { value: '小店区', label: '小店区' },
          { value: '迎泽区', label: '迎泽区' }
        ]
      }
    ]
  },
  {
    value: '内蒙古自治区',
    label: '内蒙古自治区',
    children: [
      {
        value: '呼和浩特市',
        label: '呼和浩特市',
        children: [
          { value: '回民区', label: '回民区' },
          { value: '新城区', label: '新城区' }
        ]
      },
      {
        value: '包头市',
        label: '包头市'
      }
    ]
  },
  {
    value: '黑龙江省',
    label: '黑龙江省',
    children: [
      {
        value: '哈尔滨市',
        label: '哈尔滨市',
        children: [
          { value: '南岗区', label: '南岗区' },
          { value: '道里区', label: '道里区' }
        ]
      }
    ]
  },
  {
    value: '吉林省',
    label: '吉林省',
    children: [
      {
        value: '长春市',
        label: '长春市',
        children: [
          { value: '朝阳区', label: '朝阳区' },
          { value: '南关区', label: '南关区' }
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

// 获取职位列表
const fetchJobList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      jobName: searchForm.jobName || undefined,
      company: searchForm.company || undefined,
      status: searchForm.status || undefined
    }
    console.log('Fetching jobs with params:', params)
    
    const response = await getJobList(params)
    console.log('Full API response:', response)

    if (response?.content) {
      // 处理数据
      jobList.value = response.content.map(job => ({
        ...job,
        tags: job.tags ? job.tags.split(',') : [],
        location: job.location ? job.location.split(' / ') : []
      }))
      
      // 设置总数
      total.value = response.totalElements
      console.log('Processed job list:', jobList.value)
    } else {
      jobList.value = []
      total.value = 0
      console.warn('No data received from API')
    }
  } catch (error) {
    console.error('获取职位列表失败:', error)
    ElMessage.error('获取职位列表失败')
    jobList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchJobList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.jobName = ''
  searchForm.company = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchJobList()
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

// 新增职位
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(jobForm, {
    jobName: '',
    company: '',
    department: '',
    location: [],
    salaryRange: '',
    jobType: 'fulltime',
    experience: '',
    education: '',
    tags: [],
    description: '',
    requirements: '',
    status: '0'
  })
}

// 编辑职位
const handleEdit = async (row: InternalJob) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  // 处理location字符串转数组，确保类型安全
  const locationArray = typeof row.location === 'string' ? row.location.split(' / ') : (Array.isArray(row.location) ? row.location : [])
  
  // 确保所有字段都被正确赋值
  Object.assign(jobForm, {
    id: row.id,
    jobName: row.jobName || '',
    company: row.company || '',
    department: row.department || '',
    location: locationArray,
    salaryRange: row.salaryRange || '',
    jobType: row.jobType || 'fulltime',
    experience: row.experience || '',
    education: row.education || '',
    tags: Array.isArray(row.tags) ? row.tags : (typeof row.tags === 'string' ? row.tags.split(',') : []),
    description: row.description || '',
    requirements: row.requirements || '',
    status: row.status || '0'
  })
}

// 在 handleSubmit 函数之前添加格式化地点的函数
const formatLocation = (locationArray: string[]) => {
  if (!locationArray || !Array.isArray(locationArray)) return ''
  return locationArray.join(' / ')
}

// 修改 handleSubmit 函数
const handleSubmit = async () => {
  if (!jobFormRef.value) return
  
  await jobFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 处理表单数据
        const submitData = {
          ...jobForm,
          tags: Array.isArray(jobForm.tags) ? jobForm.tags.join(',') : jobForm.tags,
          location: formatLocation(jobForm.location as string[]) // 格式化地点数据
        }

        if (dialogType.value === 'add') {
          await createJob(submitData)
          ElMessage.success('发布成功')
        } else {
          const currentJob = jobList.value.find(job => job.jobName === jobForm.jobName)
          if (currentJob) {
            await updateJob(currentJob.id, submitData)
            ElMessage.success('修改成功')
          }
        }
        
        dialogVisible.value = false
        fetchJobList()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 切换职位状态
const handleToggleStatus = async (row: Job) => {
  const action = row.status === '1' ? '下线' : '发布'
  const newStatus = row.status === '1' ? '0' : '1'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}该职位吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateJobStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    row.status = newStatus
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新职位状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 删除职位
const handleDelete = async (row: Job) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该职位吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteJob(row.id)
    ElMessage.success('删除成功')
    fetchJobList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除职位失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 查看投递记录
const viewApplications = (row: Job) => {
  router.push({
    name: 'ApplicationManagement',
    query: { jobId: row.id }
  })
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '1': 'success',
    '0': 'info',
    '2': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    '1': '已发布',
    '0': '未发布',
    '2': '已下线'
  }
  return textMap[status] || status
}

// 日期格式化函数
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchJobList()
})
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

  .el-tag {
    margin-right: 4px;
    margin-bottom: 4px;
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