<template>
  <div class="resume-container">
    <el-card class="resume-card">
      <template #header>
        <div class="card-header">
          <span>简历管理</span>
          <el-button type="primary" @click="handleUpload">上传简历</el-button>
        </div>
      </template>

      <el-table :data="resumes" v-loading="loading">
        <el-table-column prop="name" label="简历名称" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column prop="status" label="分析状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">
              {{ row.status === 'completed' ? '已完成' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              :disabled="row.status !== 'completed'"
              @click="handleView(row)"
            >
              查看分析
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="analysisVisible"
      title="简历分析结果"
      width="60%"
    >
      <div class="analysis-content" v-if="currentAnalysis">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="技能评估">
            <el-tag
              v-for="(skill, index) in currentAnalysis.skills"
              :key="index"
              class="skill-tag"
            >
              {{ skill }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="工作经验匹配度">
            <el-progress
              :percentage="currentAnalysis.experienceMatch"
              :format="format"
            />
          </el-descriptions-item>
          <el-descriptions-item label="建议">
            <div class="suggestions">
              <p v-for="(suggestion, index) in currentAnalysis.suggestions" :key="index">
                {{ suggestion }}
              </p>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const resumes = ref([])
const analysisVisible = ref(false)
const currentAnalysis = ref(null)

const format = (percentage) => percentage + '%'

const fetchResumes = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/resumes')
    resumes.value = response.data
  } catch (error) {
    console.error('获取简历列表失败:', error)
    ElMessage.error('获取简历列表失败')
  } finally {
    loading.value = false
  }
}

const handleUpload = () => {
  // 实现文件上传逻辑
}

const handleView = async (resume) => {
  try {
    const response = await axios.get(`/api/resumes/${resume.id}/analysis`)
    currentAnalysis.value = response.data
    analysisVisible.value = true
  } catch (error) {
    console.error('获取分析结果失败:', error)
    ElMessage.error('获取分析结果失败')
  }
}

const handleDelete = async (resume) => {
  try {
    await ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
      type: 'warning'
    })
    await axios.delete(`/api/resumes/${resume.id}`)
    ElMessage.success('删除成功')
    fetchResumes()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除简历失败:', error)
      ElMessage.error('删除简历失败')
    }
  }
}

fetchResumes()
</script>

<style lang="scss" scoped>
.resume-container {
  padding: 20px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .analysis-content {
    .skill-tag {
      margin-right: 8px;
      margin-bottom: 8px;
    }

    .suggestions {
      p {
        margin: 8px 0;
        line-height: 1.5;
      }
    }
  }
}
</style>