<template>
  <div class="resume-green">
    <header class="header">
      <h1>{{ person.name.first + ' ' + person.name.last }}</h1>
      <h2>{{ person.position }}</h2>
      <div class="birth-info">
        {{ lang.bornIn }} {{ person.birth.location }}, {{ person.birth.year }}
      </div>
    </header>

    <main class="content">
      <section class="about">
        <h3>{{ lang.about }}</h3>
        <p>{{ person.about }}</p>
        <p>{{ person.knowledge }}</p>
      </section>

      <section class="skills">
        <h3>{{ lang.skills }}</h3>
        <div class="skills-grid">
          <div v-for="skill in person.skills" :key="skill.name" class="skill-item">
            <span class="skill-name">{{ skill.name }}</span>
            <div class="skill-bar">
              <div class="skill-level" :style="{ width: skill.level + '%' }"></div>
            </div>
          </div>
        </div>
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
    </main>

    <aside class="contact">
      <h3>{{ lang.contact }}</h3>
      <ul>
        <li><i class="fa fa-map-marker"></i> {{ person.contact.street }}</li>
        <li><i class="fa fa-phone"></i> {{ person.contact.phone }}</li>
        <li><i class="fa fa-envelope"></i> {{ person.contact.email }}</li>
        <li><i class="fa fa-globe"></i> {{ person.contact.website }}</li>
        <li><i class="fa fa-github"></i> {{ person.contact.github }}</li>
      </ul>
    </aside>
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
.resume-green {
  padding: 40px;
  color: #2c3e50;
  font-family: 'Roboto', sans-serif;
  position: relative;
}

.header {
  background: #4CAF50;
  color: white;
  padding: 30px;
  margin: -40px -40px 30px -40px;
}

.header h1 {
  margin: 0;
  font-size: 2.5em;
}

.header h2 {
  margin: 10px 0;
  font-weight: 300;
}

.birth-info {
  font-size: 0.9em;
  opacity: 0.9;
}

.content {
  margin-right: 220px;
}

section {
  margin-bottom: 30px;
}

h3 {
  color: #4CAF50;
  border-bottom: 2px solid #4CAF50;
  padding-bottom: 5px;
  margin-bottom: 15px;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.skill-item {
  margin-bottom: 10px;
}

.skill-name {
  display: block;
  margin-bottom: 5px;
}

.skill-bar {
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
}

.skill-level {
  height: 100%;
  background: #4CAF50;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.exp-item, .edu-item {
  margin-bottom: 20px;
}

.exp-details, .edu-details {
  color: #666;
  font-size: 0.9em;
  margin: 5px 0;
}

.exp-details span, .edu-details span {
  margin-right: 15px;
}

.contact {
  position: absolute;
  right: 40px;
  top: 200px;
  width: 200px;
}

.contact ul {
  list-style: none;
  padding: 0;
}

.contact li {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  font-size: 0.9em;
}

.contact i {
  margin-right: 10px;
  color: #4CAF50;
  width: 20px;
}

@media print {
  .resume-green {
    padding: 20px;
  }
  
  .header {
    margin: -20px -20px 30px -20px;
  }
}
</style>
