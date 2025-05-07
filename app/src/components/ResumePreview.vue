<template>
  <div class="resume-preview">
    <component 
      :is="currentTemplate" 
      :person="resumeData"
      :lang="lang"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { resumeComponents } from './resumes/resumes'

type ResumeTemplate = keyof typeof resumeComponents

const props = defineProps({
  template: {
    type: String as () => ResumeTemplate,
    required: true
  },
  resumeData: {
    type: Object,
    required: true
  }
})

const lang = {
  experience: '工作经验',
  education: '教育经历',
  skills: '技能特长',
  about: '关于我',
  knowledge: '知识储备',
  projects: '项目经历',
  contact: '联系方式',
  born: '出生于',
  bornIn: '在',
  contributions: '贡献',
  additionalSkills: '其他技能'
}

const currentTemplate = computed(() => {
  return resumeComponents[props.template]
})
</script>

<style scoped>
.resume-preview {
  width: 100%;
  height: 100%;
  overflow: auto;
  background: #f5f5f5;
  padding: 20px;
}
</style> 