<template>
  <div class="resume-container">
    <el-card class="mb-20">
      <template #header>
        <div class="card-header">
          <h3>我的简历</h3>
          <el-button type="primary" @click="saveResume">保存简历</el-button>
        </div>
      </template>

      <el-form :model="resumeForm" label-width="100px">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="resumeForm.basicInfo.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="resumeForm.basicInfo.age" :min="16" :max="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话">
              <el-input v-model="resumeForm.basicInfo.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="resumeForm.basicInfo.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 教育经历 -->
        <el-divider content-position="left">
          <div class="divider-header">
            <span>教育经历</span>
            <el-button type="primary" link @click="addEducation">
              <el-icon><Plus /></el-icon>添加教育经历
            </el-button>
          </div>
        </el-divider>
        
        <div v-for="(edu, index) in resumeForm.education" :key="index" class="experience-item">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="'学校名称'">
                <el-input v-model="edu.school" placeholder="请输入学校名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="'专业'">
                <el-input v-model="edu.major" placeholder="请输入专业" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="'学历'">
                <el-select v-model="edu.degree" placeholder="请选择学历">
                  <el-option label="专科" value="专科" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="'在读时间'">
                <el-date-picker
                  v-model="edu.time"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  format="YYYY-MM"
                  value-format="YYYY-MM"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-button type="danger" link class="delete-btn" @click="removeEducation(index)">
            删除
          </el-button>
          <el-divider v-if="index !== resumeForm.education.length - 1" />
        </div>

        <!-- 工作经历 -->
        <el-divider content-position="left">
          <div class="divider-header">
            <span>工作经历</span>
            <el-button type="primary" link @click="addWork">
              <el-icon><Plus /></el-icon>添加工作经历
            </el-button>
          </div>
        </el-divider>
        
        <div v-for="(work, index) in resumeForm.work" :key="index" class="experience-item">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="'公司名称'">
                <el-input v-model="work.company" placeholder="请输入公司名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="'职位'">
                <el-input v-model="work.position" placeholder="请输入职位" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="'工作时间'">
                <el-date-picker
                  v-model="work.time"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  format="YYYY-MM"
                  value-format="YYYY-MM"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item :label="'工作内容'">
                <el-input
                  v-model="work.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入工作内容描述"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-button type="danger" link class="delete-btn" @click="removeWork(index)">
            删除
          </el-button>
          <el-divider v-if="index !== resumeForm.work.length - 1" />
        </div>

        <!-- 技能特长 -->
        <el-divider content-position="left">技能特长</el-divider>
        <el-form-item>
          <el-tag
            v-for="(tag, index) in resumeForm.skills"
            :key="index"
            class="skill-tag"
            closable
            @close="removeSkill(index)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            class="input-new-tag"
            size="small"
            @keyup.enter="addSkill"
            @blur="addSkill"
          />
          <el-button v-else class="button-new-tag" size="small" @click="showInput">
            <el-icon><Plus /></el-icon> 添加技能
          </el-button>
        </el-form-item>

        <!-- 附件上传 -->
        <el-divider content-position="left">附件简历</el-divider>
        <el-upload
          class="upload-demo"
          drag
          action="/api/upload"
          accept=".pdf"
          :limit="1"
          :on-success="handleUploadSuccess"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 PDF 文件
            </div>
          </template>
        </el-upload>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadProps } from 'element-plus'

interface Education {
  school: string
  major: string
  degree: string
  time: [string, string]
}

interface Work {
  company: string
  position: string
  time: [string, string]
  description: string
}

interface ResumeForm {
  basicInfo: {
    name: string
    age: number
    phone: string
    email: string
  }
  education: Education[]
  work: Work[]
  skills: string[]
}

const resumeForm = reactive<ResumeForm>({
  basicInfo: {
    name: '',
    age: 20,
    phone: '',
    email: ''
  },
  education: [],
  work: [],
  skills: []
})

// 教育经历
const addEducation = () => {
  resumeForm.education.push({
    school: '',
    major: '',
    degree: '',
    time: ['', '']
  })
}

const removeEducation = (index: number) => {
  resumeForm.education.splice(index, 1)
}

// 工作经历
const addWork = () => {
  resumeForm.work.push({
    company: '',
    position: '',
    time: ['', ''],
    description: ''
  })
}

const removeWork = (index: number) => {
  resumeForm.work.splice(index, 1)
}

// 技能标签
const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<HTMLInputElement>()

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value?.focus()
  })
}

const addSkill = () => {
  if (inputValue.value) {
    resumeForm.skills.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

const removeSkill = (index: number) => {
  resumeForm.skills.splice(index, 1)
}

// 保存简历
const saveResume = () => {
  console.log('保存简历:', resumeForm)
  ElMessage.success('简历保存成功！')
}

// 上传简历
const handleUploadSuccess: UploadProps['onSuccess'] = (response) => {
  ElMessage.success('文件上传成功！')
}
</script>

<style lang="scss" scoped>
.resume-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
    }
  }
  
  .divider-header {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .experience-item {
    position: relative;
    padding: 20px;
    border-radius: 4px;
    
    &:hover {
      background-color: var(--el-fill-color-light);
      
      .delete-btn {
        display: block;
      }
    }
    
    .delete-btn {
      display: none;
      position: absolute;
      top: 10px;
      right: 10px;
    }
  }
  
  .skill-tag {
    margin-right: 10px;
    margin-bottom: 10px;
  }
  
  .input-new-tag {
    width: 90px;
    margin-right: 10px;
    vertical-align: bottom;
  }
  
  .el-upload__tip {
    color: var(--el-text-color-secondary);
    font-size: 14px;
    margin-top: 8px;
  }
}
</style> 