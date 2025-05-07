<template>
  <div class="resume-oblique">
    <div class="diagonal-bg"></div>
    
    <header class="header">
      <div class="header-content">
        <h1>{{ person.name.first + ' ' + person.name.last }}</h1>
        <h2>{{ person.position }}</h2>
        <div class="birth-info">
          {{ lang.bornIn }} {{ person.birth.location }}, {{ person.birth.year }}
        </div>
      </div>
    </header>

    <main class="main-content">
      <div class="left-content">
        <section class="contact">
          <h3>{{ lang.contact }}</h3>
          <ul>
            <li><i class="fa fa-map-marker"></i> {{ person.contact.street }}</li>
            <li><i class="fa fa-phone"></i> {{ person.contact.phone }}</li>
            <li><i class="fa fa-envelope"></i> {{ person.contact.email }}</li>
            <li><i class="fa fa-globe"></i> {{ person.contact.website }}</li>
            <li><i class="fa fa-github"></i> {{ person.contact.github }}</li>
          </ul>
        </section>

        <section class="skills">
          <h3>{{ lang.skills }}</h3>
          <div class="skill-list">
            <div v-for="skill in person.skills" :key="skill.name" class="skill">
              <div class="skill-info">
                <span class="skill-name">{{ skill.name }}</span>
                <span class="skill-percentage">{{ skill.level }}%</span>
              </div>
              <div class="skill-bar">
                <div class="skill-level" :style="{ width: skill.level + '%' }"></div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <div class="right-content">
        <section class="about">
          <h3>{{ lang.about }}</h3>
          <p>{{ person.about }}</p>
          <p class="knowledge">{{ person.knowledge }}</p>
        </section>

        <section class="experience">
          <h3>{{ lang.experience }}</h3>
          <div class="timeline">
            <div v-for="exp in person.experience" :key="exp.company" class="timeline-item">
              <div class="timeline-marker"></div>
              <div class="timeline-content">
                <h4>{{ exp.position }}</h4>
                <div class="company">{{ exp.company }}</div>
                <div class="meta">
                  <span class="period">{{ exp.timeperiod }}</span>
                  <span class="location">{{ exp.location }}</span>
                </div>
                <p class="description">{{ exp.description }}</p>
              </div>
            </div>
          </div>
        </section>

        <section class="education">
          <h3>{{ lang.education }}</h3>
          <div class="timeline">
            <div v-for="edu in person.education" :key="edu.school" class="timeline-item">
              <div class="timeline-marker"></div>
              <div class="timeline-content">
                <h4>{{ edu.school }}</h4>
                <div class="degree">{{ edu.degree }}</div>
                <div class="meta">
                  <span class="period">{{ edu.timeperiod }}</span>
                </div>
                <p class="description">{{ edu.description }}</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
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
.resume-oblique {
  position: relative;
  min-height: 100%;
  background: white;
  font-family: 'Roboto', sans-serif;
  overflow: hidden;
  padding: 40px;
}

.diagonal-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f5f5 0%, #f5f5f5 50%, transparent 50%);
  z-index: 0;
}

.header {
  position: relative;
  z-index: 1;
  text-align: center;
  margin-bottom: 50px;
}

.header-content {
  display: inline-block;
  padding: 20px 40px;
  background: #2196F3;
  color: white;
  transform: skew(-15deg);
}

.header-content > * {
  transform: skew(15deg);
}

h1 {
  margin: 0;
  font-size: 2.5em;
  font-weight: 300;
}

h2 {
  margin: 10px 0 5px;
  font-size: 1.2em;
  font-weight: 400;
  opacity: 0.9;
}

.birth-info {
  font-size: 0.9em;
  opacity: 0.8;
}

.main-content {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 40px;
}

.left-content {
  width: 300px;
}

.right-content {
  flex: 1;
}

section {
  margin-bottom: 40px;
}

h3 {
  color: #2196F3;
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
  background: #2196F3;
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
  color: #2196F3;
}

.skill {
  margin-bottom: 20px;
}

.skill-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.skill-name {
  color: #2c3e50;
}

.skill-percentage {
  color: #666;
  font-size: 0.9em;
}

.skill-bar {
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  overflow: hidden;
}

.skill-level {
  height: 100%;
  background: #2196F3;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e0e0e0;
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
}

.timeline-marker {
  position: absolute;
  left: -34px;
  top: 5px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #2196F3;
  border: 2px solid white;
}

.timeline-content {
  background: white;
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

h4 {
  color: #2c3e50;
  margin: 0 0 5px;
  font-size: 1.1em;
}

.company, .degree {
  color: #2196F3;
  font-weight: 500;
  margin-bottom: 5px;
}

.meta {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 10px;
}

.period {
  margin-right: 15px;
}

.description {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.knowledge {
  color: #666;
  font-style: italic;
  margin-top: 10px;
}

@media print {
  .resume-oblique {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
  
  .timeline-content {
    box-shadow: none;
    border: 1px solid #eee;
  }
}
</style>
