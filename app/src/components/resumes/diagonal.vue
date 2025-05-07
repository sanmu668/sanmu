<template>
  <div class="resume-diagonal">
    <div class="diagonal-background"></div>
    
    <header class="header">
      <div class="header-content">
        <h1>{{ person.name.first + ' ' + person.name.last }}</h1>
        <h2>{{ person.position }}</h2>
      </div>
    </header>

    <main class="content">
      <div class="left-section">
        <section class="about">
          <h3>{{ lang.about }}</h3>
          <p>{{ person.about }}</p>
          <p>{{ person.knowledge }}</p>
        </section>

        <section class="experience">
          <h3>{{ lang.experience }}</h3>
          <div v-for="exp in person.experience" :key="exp.company" class="exp-item">
            <h4>{{ exp.company }}</h4>
            <div class="exp-details">
              <span class="position">{{ exp.position }}</span>
              <span class="timeperiod">{{ exp.timeperiod }}</span>
              <span class="location">{{ exp.location }}</span>
            </div>
            <p>{{ exp.description }}</p>
          </div>
        </section>
      </div>

      <div class="right-section">
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
          <div class="skills-list">
            <div v-for="skill in person.skills" :key="skill.name" class="skill-item">
              <span class="skill-name">{{ skill.name }}</span>
              <div class="skill-bar">
                <div class="skill-level" :style="{ width: skill.level + '%' }"></div>
              </div>
            </div>
          </div>
        </section>

        <section class="education">
          <h3>{{ lang.education }}</h3>
          <div v-for="edu in person.education" :key="edu.school" class="edu-item">
            <h4>{{ edu.school }}</h4>
            <div class="edu-details">
              <span class="degree">{{ edu.degree }}</span>
              <span class="timeperiod">{{ edu.timeperiod }}</span>
            </div>
            <p>{{ edu.description }}</p>
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
.resume-diagonal {
  padding: 0;
  color: #2c3e50;
  font-family: 'Roboto', sans-serif;
  position: relative;
  overflow: hidden;
  min-height: 100vh;
}

.diagonal-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f5f5f5 0%, #f5f5f5 50%, #ffffff 50%, #ffffff 100%);
  z-index: -1;
}

.header {
  padding: 40px;
  position: relative;
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
}

.header h1 {
  margin: 0;
  font-size: 3em;
  color: #2c3e50;
  font-weight: 700;
}

.header h2 {
  margin: 10px 0;
  font-size: 1.5em;
  color: #3498db;
  font-weight: 400;
}

.content {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px 40px;
  gap: 60px;
}

.left-section {
  flex: 2;
}

.right-section {
  flex: 1;
}

section {
  margin-bottom: 40px;
}

h3 {
  color: #2c3e50;
  font-size: 1.4em;
  border-bottom: 2px solid #3498db;
  padding-bottom: 8px;
  margin-bottom: 20px;
}

.exp-item, .edu-item {
  margin-bottom: 25px;
}

.exp-item h4, .edu-item h4 {
  color: #2c3e50;
  margin: 0 0 5px 0;
}

.exp-details, .edu-details {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 10px;
}

.exp-details span, .edu-details span {
  margin-right: 15px;
  display: inline-block;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.skill-item {
  margin-bottom: 10px;
}

.skill-name {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.skill-bar {
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  overflow: hidden;
}

.skill-level {
  height: 100%;
  background: #3498db;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.contact ul {
  list-style: none;
  padding: 0;
}

.contact li {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  font-size: 0.9em;
}

.contact i {
  margin-right: 10px;
  color: #3498db;
  width: 20px;
}

@media print {
  .resume-diagonal {
    min-height: auto;
  }
  
  .content {
    padding: 20px;
  }
  
  .header {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;
    padding: 20px;
  }
  
  .right-section {
    order: -1;
  }
}
</style> 