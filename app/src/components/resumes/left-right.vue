<template>
  <div class="resume-left-right">
    <div class="left-section">
      <div class="profile">
        <h1>{{ person.name.first + ' ' + person.name.last }}</h1>
        <h2>{{ person.position }}</h2>
        <div class="birth-info">
          {{ lang.bornIn }} {{ person.birth.location }}, {{ person.birth.year }}
        </div>
      </div>

      <div class="contact">
        <h3>{{ lang.contact }}</h3>
        <ul>
          <li><i class="fa fa-map-marker"></i> {{ person.contact.street }}</li>
          <li><i class="fa fa-phone"></i> {{ person.contact.phone }}</li>
          <li><i class="fa fa-envelope"></i> {{ person.contact.email }}</li>
          <li><i class="fa fa-globe"></i> {{ person.contact.website }}</li>
          <li><i class="fa fa-github"></i> {{ person.contact.github }}</li>
        </ul>
      </div>

      <div class="skills">
        <h3>{{ lang.skills }}</h3>
        <div class="skill-list">
          <div v-for="skill in person.skills" :key="skill.name" class="skill">
            <div class="skill-header">
              <span class="skill-name">{{ skill.name }}</span>
              <span class="skill-level-text">{{ skill.level }}%</span>
            </div>
            <div class="skill-bar">
              <div class="skill-level" :style="{ width: skill.level + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="right-section">
      <section class="about">
        <h3>{{ lang.about }}</h3>
        <div class="section-content">
          <p>{{ person.about }}</p>
          <p class="knowledge">{{ person.knowledge }}</p>
        </div>
      </section>

      <section class="experience">
        <h3>{{ lang.experience }}</h3>
        <div class="section-content">
          <div v-for="exp in person.experience" :key="exp.company" class="experience-item">
            <div class="item-header">
              <h4>{{ exp.position }}</h4>
              <span class="company">{{ exp.company }}</span>
            </div>
            <div class="item-subheader">
              <span class="period">{{ exp.timeperiod }}</span>
              <span class="location">{{ exp.location }}</span>
            </div>
            <p class="description">{{ exp.description }}</p>
          </div>
        </div>
      </section>

      <section class="education">
        <h3>{{ lang.education }}</h3>
        <div class="section-content">
          <div v-for="edu in person.education" :key="edu.school" class="education-item">
            <div class="item-header">
              <h4>{{ edu.school }}</h4>
              <span class="degree">{{ edu.degree }}</span>
            </div>
            <div class="item-subheader">
              <span class="period">{{ edu.timeperiod }}</span>
            </div>
            <p class="description">{{ edu.description }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
defineProps({
  person: {
    type: Object,
    required: true
  },
  lang: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.resume-left-right {
  display: flex;
  min-height: 100%;
  font-family: 'Roboto', sans-serif;
  background: white;
}

.left-section {
  width: 35%;
  background: #f5f5f5;
  padding: 40px;
}

.right-section {
  flex: 1;
  padding: 40px;
  background: white;
}

.profile {
  text-align: center;
  margin-bottom: 40px;
}

.profile h1 {
  margin: 0;
  font-size: 1.8em;
  color: #2c3e50;
}

.profile h2 {
  margin: 10px 0;
  font-size: 1.2em;
  color: #666;
  font-weight: 400;
}

.birth-info {
  font-size: 0.9em;
  color: #666;
}

.contact, .skills {
  margin-bottom: 40px;
}

h3 {
  color: #2c3e50;
  font-size: 1.3em;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}

h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: #3498db;
}

.contact ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.contact li {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  color: #666;
}

.contact i {
  width: 20px;
  margin-right: 10px;
  color: #3498db;
}

.skill {
  margin-bottom: 20px;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.skill-name {
  color: #2c3e50;
}

.skill-level-text {
  color: #666;
  font-size: 0.9em;
}

.skill-bar {
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
}

.skill-level {
  height: 100%;
  background: #3498db;
  border-radius: 3px;
  transition: width 0.3s ease;
}

section {
  margin-bottom: 40px;
}

.section-content {
  color: #666;
}

.item-header {
  margin-bottom: 5px;
}

h4 {
  color: #2c3e50;
  margin: 0;
  font-size: 1.1em;
  display: inline-block;
}

.company, .degree {
  color: #3498db;
  font-weight: 500;
  margin-left: 10px;
}

.item-subheader {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 10px;
}

.period {
  margin-right: 15px;
}

.description {
  margin: 0;
  line-height: 1.5;
}

.experience-item, .education-item {
  margin-bottom: 25px;
  padding-bottom: 25px;
  border-bottom: 1px solid #eee;
}

.experience-item:last-child,
.education-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.knowledge {
  font-style: italic;
  margin-top: 10px;
}

@media print {
  .resume-left-right {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
}
</style>
