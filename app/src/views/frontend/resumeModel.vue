<template>
  <div class="page-wrapper">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="template-selector">
        <el-radio-group v-model="selectedTemplate" class="template-list">
          <el-radio-button v-for="(_, name) in templates" :key="name" :label="name">
            {{ getTemplateName(name) }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="actions">
        <el-button type="primary" @click="openEditDrawer">
          编辑内容
        </el-button>
        <el-button type="success" @click="saveResume" :loading="generating">
          {{ generating ? '正在生成PDF...' : '保存简历' }}
        </el-button>
      </div>
    </div>
    
    <div class="page">
      <div class="page-inner">
        <!-- 动态加载模板 -->
        <component 
          v-if="templates[selectedTemplate]"
          :is="templates[selectedTemplate]" 
          :person="resumeData" 
          :lang="defaultLang" 
        />
        <div v-else class="error-message">
          <el-alert
            title="模板加载失败"
            type="error"
            description="所选模板不存在或加载失败，请检查控制台错误信息"
            show-icon
          />
        </div>
      </div>
    </div>

    <!-- 编辑抽屉 -->
    <el-drawer
      v-model="editDrawerVisible"
      title="编辑简历内容"
      direction="rtl"
      size="500px"
    >
      <el-form :model="resumeData" label-width="100px">
        <!-- 基本信息 -->
        <el-form-item label="姓名">
          <el-input v-model="resumeData.name.first" placeholder="姓" style="width: 80px" />
          <el-input v-model="resumeData.name.middle" placeholder="中间名" style="width: 100px" />
          <el-input v-model="resumeData.name.last" placeholder="名" style="width: 80px" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="resumeData.position" />
        </el-form-item>
        <el-form-item label="出生信息">
          <el-input v-model="resumeData.birth.year" placeholder="年份" style="width: 100px" />
          <el-input v-model="resumeData.birth.location" placeholder="地点" style="width: 200px" />
        </el-form-item>
        
        <!-- 关于我 -->
        <el-form-item label="关于我">
          <el-input v-model="resumeData.about" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="专业技能">
          <el-input v-model="resumeData.knowledge" type="textarea" :rows="3" />
        </el-form-item>

        <!-- 联系方式 -->
        <el-form-item label="联系方式">
          <el-input v-model="resumeData.contact.street" placeholder="地址" />
          <el-input v-model="resumeData.contact.phone" placeholder="电话" />
          <el-input v-model="resumeData.contact.email" placeholder="邮箱" />
          <el-input v-model="resumeData.contact.website" placeholder="个人网站" />
          <el-input v-model="resumeData.contact.github" placeholder="GitHub" />
        </el-form-item>

        <!-- 技能列表 -->
        <el-form-item label="技能">
          <div v-for="(skill, index) in resumeData.skills" :key="index" style="margin-bottom: 10px;">
            <el-input v-model="skill.name" placeholder="技能名称" style="width: 200px" />
            <el-input-number v-model="skill.level" :min="0" :max="100" />
            <el-button type="danger" @click="removeSkill(index)">删除</el-button>
          </div>
          <el-button type="primary" @click="addSkill">添加技能</el-button>
        </el-form-item>

        <!-- 工作经历 -->
        <el-form-item label="工作经历">
          <div v-for="(exp, index) in resumeData.experience" :key="index" class="experience-item">
            <el-input v-model="exp.company" placeholder="公司名称" />
            <el-input v-model="exp.position" placeholder="职位" />
            <el-input v-model="exp.timeperiod" placeholder="时间段" />
            <el-input v-model="exp.location" placeholder="地点" />
            <el-input v-model="exp.description" type="textarea" placeholder="工作描述" :rows="2" />
            <el-button type="danger" @click="removeExperience(index)">删除</el-button>
          </div>
          <el-button type="primary" @click="addExperience">添加工作经历</el-button>
        </el-form-item>

        <!-- 教育经历 -->
        <el-form-item label="教育经历">
          <div v-for="(edu, index) in resumeData.education" :key="index" class="education-item">
            <el-input v-model="edu.school" placeholder="学校" />
            <el-input v-model="edu.degree" placeholder="学位/专业" />
            <el-input v-model="edu.timeperiod" placeholder="时间段" />
            <el-input v-model="edu.description" type="textarea" placeholder="描述" :rows="2" />
            <el-button type="danger" @click="removeEducation(index)">删除</el-button>
          </div>
          <el-button type="primary" @click="addEducation">添加教育经历</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </div>
</template>

<script setup>
import html2pdf from 'html2pdf.js/dist/html2pdf.bundle.min.js'

import { ref, computed, watch, onMounted, defineAsyncComponent, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElAlert, ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import { API_CONFIG, API_ENDPOINTS } from '@/config'

const generating = ref(false)
// 使用异步组件导入模板


const templates = ref({
  cool: defineAsyncComponent({
    loader: () => import('@/components/resumes/cool.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  }),
  creative: defineAsyncComponent({
    loader: () => import('@/components/resumes/creative.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  }),
  green: defineAsyncComponent({
    loader: () => import('@/components/resumes/green.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  }),
  purple: defineAsyncComponent({
    loader: () => import('@/components/resumes/purple.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  }),
  'material-dark': defineAsyncComponent({
    loader: () => import('@/components/resumes/material-dark.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  }),
  'left-right': defineAsyncComponent({
    loader: () => import('@/components/resumes/left-right.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  }),
  oblique: defineAsyncComponent({
    loader: () => import('@/components/resumes/oblique.vue'),
    loadingComponent: h('div', { class: 'loading' }, 'Loading...'),
    errorComponent: h('div', { class: 'error' }, 'Failed to load template'),
    delay: 200,
    timeout: 3000
  })
})

// 定义默认的简历数据
const defaultPerson = {
  name: {
    first: '张',
    middle: '',
    last: '三'
  },
  position: '高级前端开发工程师',
  birth: {
    year: '1990',
    location: '北京'
  },
  about: '富有创造力和责任心的前端开发工程师，拥有5年以上的开发经验。',
  knowledge: '精通 Vue.js、React.js 等前端框架，具有丰富的大型应用开发经验。',
  contact: {
    street: '北京市朝阳区xxx街道',
    phone: '13800138000',
    email: 'zhangsan@example.com',
    website: 'https://example.com',
    github: 'github.com/zhangsan'
  },
  skills: [
    { name: 'Vue.js', level: 95 },
    { name: 'React', level: 85 },
    { name: 'TypeScript', level: 90 },
    { name: 'Node.js', level: 85 },
    { name: 'Webpack', level: 80 }
  ],
  experience: [
    {
      company: 'ABC科技有限公司',
      position: '高级前端开发工程师',
      timeperiod: '2020 - 至今',
      description: '负责公司核心产品的前端架构设计和开发',
      location: '北京'
    }
  ],
  education: [
    {
      school: '北京大学',
      degree: '计算机科学与技术',
      timeperiod: '2010 - 2014',
      description: '主修计算机科学与技术，辅修数学'
    }
  ]
}

// 定义语言包
const defaultLang = {
  born: '出生于',
  bornIn: '在',
  about: '关于我',
  skills: '技能专长',
  additionalSkills: '其他技能',
  contact: '联系方式',
  experience: '工作经历',
  education: '教育背景',
  projects: '项目经历',
  contributions: '个人贡献'
}

const route = useRoute()
const router = useRouter()

// 从路由参数获取模板名，默认使用cool模板
const selectedTemplate = ref(route.params.resumeid || 'cool')

// 监听模板选择变化
watch(selectedTemplate, (newTemplate) => {
  console.log('Template changed to:', newTemplate)
  router.push(`/resumeModel/${newTemplate}`)
})

// 获取模板显示名称
const getTemplateName = (name) => {
  const nameMap = {
    cool: '清新模板',
    creative: '创意模板',
    green: '绿色模板',
    purple: '紫色模板',
    'material-dark': '暗色模板',
    'left-right': '左右模板',
    oblique: '斜线模板'
  }
  return nameMap[name] || name
}

// 编辑功能相关的状态
const editDrawerVisible = ref(false)
const resumeData = ref({ ...defaultPerson }) // 使用解构复制，避免直接修改默认数据

// 打开编辑抽屉
const openEditDrawer = () => {
  editDrawerVisible.value = true
}

// 添加技能
const addSkill = () => {
  resumeData.value.skills.push({
    name: '',
    level: 80
  })
}

// 删除技能
const removeSkill = (index) => {
  resumeData.value.skills.splice(index, 1)
}

// 添加工作经历
const addExperience = () => {
  resumeData.value.experience.push({
    company: '',
    position: '',
    timeperiod: '',
    description: '',
    location: ''
  })
}

// 删除工作经历
const removeExperience = (index) => {
  resumeData.value.experience.splice(index, 1)
}

// 添加教育经历
const addEducation = () => {
  resumeData.value.education.push({
    school: '',
    degree: '',
    timeperiod: '',
    description: ''
  })
}

// 删除教育经历
const removeEducation = (index) => {
  resumeData.value.education.splice(index, 1)
}

const generatePdf = async() => {
  try {
    const element = document.querySelector('.page-inner')
    if (!element) {
      throw new Error('找不到简历内容元素')
    }

    // 创建一个临时容器来克隆内容
    const container = document.createElement('div')
    container.style.visibility = 'hidden'
    container.style.position = 'absolute'
    container.style.left = '-9999px'
    container.style.top = '-9999px'
    document.body.appendChild(container)

    // 克隆元素到临时容器
    const clone = element.cloneNode(true)
    container.appendChild(clone)

    // 确保所有样式都被加载
    const styleSheets = Array.from(document.styleSheets)
    const styles = styleSheets.reduce((acc, sheet) => {
      try {
        const rules = Array.from(sheet.cssRules || [])
        return acc + rules.map(rule => rule.cssText).join('\n')
      } catch (e) {
        console.warn('无法读取样式表:', e)
        return acc
      }
    }, '')

    const styleElement = document.createElement('style')
    styleElement.textContent = styles
    container.appendChild(styleElement)

    // 等待字体加载
    await document.fonts.ready

    // PDF配置项
    const opt = {
      margin: 0,
      filename: `${resumeData.value.name.first}${resumeData.value.name.last}的简历.pdf`,
      image: { 
        type: 'jpeg', 
        quality: 0.98
      },
      html2canvas: { 
        scale: 2,
        useCORS: true,
        allowTaint: true,
        logging: false,
        imageTimeout: 30000,
        removeContainer: true,
        foreignObjectRendering: false,
        letterRendering: true,
        scrollX: 0,
        scrollY: 0,
        windowWidth: document.documentElement.offsetWidth,
        windowHeight: document.documentElement.offsetHeight
      },
      jsPDF: { 
        unit: 'mm', 
        format: 'a4', 
        orientation: 'portrait',
        compress: true,
        precision: 16
      }
    }

    try {
      // 生成PDF数据
      const pdfBlob = await html2pdf().set(opt).from(clone).output('blob')
      document.body.removeChild(container)
      return pdfBlob
    } catch (pdfError) {
      console.error('直接生成PDF失败，尝试分步生成:', pdfError)
      
      // 如果直接生成失败，尝试分步生成
      const canvas = await html2pdf().set(opt).from(clone).toCanvas()
      if (!canvas) {
        throw new Error('Canvas生成失败')
      }

      // 将canvas转换为PDF Blob
      const pdfBlob = await html2pdf().set(opt).from(canvas).output('blob')
      
      // 清理临时容器
      document.body.removeChild(container)
      
      return pdfBlob
    }
  } catch (error) {
    console.error('PDF生成错误:', error)
    const errorMessage = error.message || '未知错误'
    throw new Error(`PDF生成失败: ${errorMessage}。请尝试刷新页面后重试，或检查网络连接。`)
  }
}

// 验证简历数据
const validateResumeData = () => {
  const { name, position, contact } = resumeData.value
  
  if (!name.first || !name.last) {
    throw new Error('请填写姓名')
  }
  if (!position) {
    throw new Error('请填写职位')
  }
  if (!contact.email) {
    throw new Error('请填写邮箱')
  }
  if (!contact.phone) {
    throw new Error('请填写电话')
  }
}

const saveResume = async () => {
  try {
    generating.value = true
    const pdfBlob = await generatePdf()
    const fileName = `${resumeData.value.name.first}${resumeData.value.name.last}的简历.pdf`

    // 创建FormData对象
    const formData = new FormData()
    
    // 将Blob转换为File对象
    const file = new File([pdfBlob], fileName, { type: 'application/pdf' })
    
    // 添加文件和其他数据到FormData
    formData.append('file', file)
    formData.append('title', `${resumeData.value.name.first}${resumeData.value.name.last}的简历`)
    formData.append('templateName', selectedTemplate.value)

    console.log('发送的请求数据:', {
      title: `${resumeData.value.name.first}${resumeData.value.name.last}的简历`,
      templateName: selectedTemplate.value,
      fileName: fileName
    })

    const token = localStorage.getItem('token')
    if (!token) {
      throw new Error('未登录状态')
    }

    const response = await request.post(
      `${API_ENDPOINTS.USER.RESUMES}/save`,
      formData,
      {
        headers: {
          // 不要设置 Content-Type，让浏览器自动处理
          Authorization: `Bearer ${token}`
        }
      }
    )

    if (response.status === 200) {
      ElMessage.success('简历保存成功')
      router.push('/resume')
    } else {
      throw new Error('保存失败：服务器返回异常状态码')
    }
  } catch (error) {
    console.error('保存简历失败:', error)
    ElMessage.error(error.message || '保存失败，请重试')
    // 如果保存到服务器失败，尝试本地下载
    try {
      await fallbackSaveResume(pdfBlob, fileName)
      ElMessage.warning('无法保存到服务器，已下载到本地')
    } catch (downloadError) {
      console.error('本地下载也失败:', downloadError)
      ElMessage.error('保存完全失败，请重试')
    }
  } finally {
    generating.value = false
  }
}

// 添加后备方案，用于不支持 File System Access API 的浏览器
const fallbackSaveResume = async(pdfBlob, fileName) => {
  // 创建下载链接
  const url = window.URL.createObjectURL(pdfBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  
  // 添加到文档并触发点击
  document.body.appendChild(link)
  link.click()
  
  // 清理
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

onMounted(async () => {
  console.log('ResumeModel component mounted')
  console.log('Current route:', route.path)
  console.log('Route params:', route.params)
  
  try {
    // 确保选中的模板有效
    if (route.params.resumeid && Object.keys(templates.value).includes(route.params.resumeid)) {
      selectedTemplate.value = route.params.resumeid
    } else if (!Object.keys(templates.value).includes(selectedTemplate.value)) {
      const firstTemplate = Object.keys(templates.value)[0]
      if (firstTemplate) {
        selectedTemplate.value = firstTemplate
        router.replace(`/resumeModel/${firstTemplate}`)
      }
    }
    
    // 预加载当前选中的模板
    const currentTemplate = templates.value[selectedTemplate.value]
    if (!currentTemplate) {
      throw new Error(`Template ${selectedTemplate.value} not found`)
    }
    
    console.log('Template loaded successfully:', selectedTemplate.value)
    
  } catch (error) {
    console.error('Failed to load template:', error)
    ElMessage.error('模板加载失败，请检查控制台错误信息')
  }
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap');
@import url('https://fonts.googleapis.com/icon?family=Material+Icons');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css');

.page-wrapper {
  overflow-x: hidden;
  background: #f0f2f5;
  margin: 0;
  padding: 40px 20px;
  -webkit-print-color-adjust: exact;
  box-sizing: border-box;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.toolbar {
  width: 100%;
  max-width: 21cm;
  margin-bottom: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.template-selector {
  margin-bottom: 20px;
}

.template-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.template-list .el-radio-button__inner {
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 14px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
}

.template-list .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background-color: #409EFF;
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.actions .el-button {
  padding: 12px 24px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.actions .el-button--primary {
  background: #409EFF;
  border-color: #409EFF;
}

.actions .el-button--primary:hover {
  background: #66b1ff;
  border-color: #66b1ff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
}

.actions .el-button--success {
  background: #67c23a;
  border-color: #67c23a;
}

.actions .el-button--success:hover {
  background: #85ce61;
  border-color: #85ce61;
  box-shadow: 0 2px 12px rgba(103, 194, 58, 0.3);
}

.page {
  background: white;
  position: relative;
  width: 21cm;
  min-height: 29.7cm;
  margin: 0 auto;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.page-inner {
  height: 100%;
  width: 100%;
  overflow: auto;
  position: relative;
}

.loading,
.error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 24px 32px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  text-align: center;
  z-index: 100;
  font-size: 16px;
  font-weight: 500;
}

.loading {
  color: #409EFF;
}

.error {
  color: #F56C6C;
}

.error-message {
  padding: 32px;
  text-align: center;
}

.error-message .el-alert {
  display: inline-flex;
  padding: 16px 24px;
}

/* 编辑抽屉样式优化 */
.el-drawer__header {
  margin-bottom: 0;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.el-drawer__body {
  padding: 20px;
}

.experience-item,
.education-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.el-form-item {
  margin-bottom: 24px;
}

@media print {
  .toolbar,
  .template-selector {
    display: none;
  }
  
  .page-wrapper {
    margin: 0;
    padding: 0;
    background: none;
  }
  
  .page {
    box-shadow: none;
    margin: 0;
    border-radius: 0;
  }
}

@media (max-width: 768px) {
  .page-wrapper {
    padding: 20px 10px;
  }
  
  .toolbar {
    padding: 15px;
  }
  
  .template-list {
    gap: 8px;
  }
  
  .template-list .el-radio-button__inner {
    padding: 8px 16px;
    font-size: 13px;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .actions .el-button {
    width: 100%;
  }
  
  .page {
    width: 100%;
  }
}
</style>