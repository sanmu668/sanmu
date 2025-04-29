<template>
  <div class="resumes-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>简历管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索简历..."
            style="width: 200px"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>
      
      <el-table :data="resumes" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="title" label="简历标题" width="200" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="experience" label="工作经验" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 表格数据
const resumes = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

// 获取简历列表
const fetchResumes = async () => {
  loading.value = true
  try {
    // TODO: 替换为实际的API调用
    const mockData = Array.from({ length: 10 }, (_, index) => ({
      id: index + 1,
      userName: `user${index + 1}`,
      title: `前端开发工程师简历${index + 1}`,
      education: '本科',
      experience: '3年',
      status: index % 2 === 0 ? 'active' : 'draft',
      updateTime: new Date().toLocaleString()
    }))
    resumes.value = mockData
    total.value = 100
  } catch (error) {
    ElMessage.error('获取简历列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResumes()
}

// 查看简历
const handleView = (row: any) => {
  ElMessage.info('查看简历功能开发中')
}

// 下载简历
const handleDownload = (row: any) => {
  ElMessage.info('下载简历功能开发中')
}

// 删除简历
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除该简历吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 分页相关
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchResumes()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchResumes()
}

onMounted(() => {
  fetchResumes()
})
</script>

<style lang="scss" scoped>
.resumes-management {
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