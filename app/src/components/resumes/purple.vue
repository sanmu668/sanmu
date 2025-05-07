<template>
  <div class="resume-purple">
    <div class="sidebar">
      <div class="profile">
        <h1>{{ person.name.first + ' ' + person.name.last }}</h1>
        <h2>{{ person.position }}</h2>
      </div>

      <div class="contact-info">
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
            <div class="skill-name">{{ skill.name }}</div>
            <div class="skill-bar">
              <div class="skill-level" :style="{ width: skill.level + '%' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <section class="about">
        <h3>{{ lang.about }}</h3>
        <p>{{ person.about }}</p>
        <p class="knowledge">{{ person.knowledge }}</p>
      </section>

      <section class="experience">
        <h3>{{ lang.experience }}</h3>
        <div v-for="exp in person.experience" :key="exp.company" class="experience-item">
          <div class="timeline-dot"></div>
          <div class="experience-content">
            <h4>{{ exp.position }}</h4>
            <div class="company">{{ exp.company }}</div>
            <div class="period">{{ exp.timeperiod }} | {{ exp.location }}</div>
            <p>{{ exp.description }}</p>
          </div>
        </div>
      </section>

      <section class="education">
        <h3>{{ lang.education }}</h3>
        <div v-for="edu in person.education" :key="edu.school" class="education-item">
          <div class="timeline-dot"></div>
          <div class="education-content">
            <h4>{{ edu.school }}</h4>
            <div class="degree">{{ edu.degree }}</div>
            <div class="period">{{ edu.timeperiod }}</div>
            <p>{{ edu.description }}</p>
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
.resume-purple {
  display: flex;
  min-height: 100%;
  font-family: 'Roboto', sans-serif;
  color: #2c3e50;
}

.sidebar {
  width: 300px;
  background: #673AB7;
  color: white;
  padding: 40px 20px;
}

.profile {
  text-align: center;
  margin-bottom: 40px;
}

.profile h1 {
  margin: 0;
  font-size: 1.8em;
  font-weight: 300;
}

.profile h2 {
  margin: 10px 0 0;
  font-size: 1.2em;
  font-weight: 300;
  opacity: 0.9;
}

.contact-info {
  margin-bottom: 40px;
}

.contact-info ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.contact-info li {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.contact-info i {
  width: 20px;
  margin-right: 10px;
}

.skills {
  margin-bottom: 40px;
}

.skill {
  margin-bottom: 15px;
}

.skill-name {
  margin-bottom: 5px;
  font-size: 0.9em;
}

.skill-bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.skill-level {
  height: 100%;
  background: white;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.main-content {
  flex: 1;
  padding: 40px;
  background: white;
}

section {
  margin-bottom: 40px;
}

h3 {
  color: #673AB7;
  margin-bottom: 20px;
  font-size: 1.5em;
  position: relative;
  padding-bottom: 10px;
}

h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 3px;
  background: #673AB7;
}

.experience-item, .education-item {
  position: relative;
  padding-left: 30px;
  margin-bottom: 30px;
}

.timeline-dot {
  position: absolute;
  left: 0;
  top: 5px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #673AB7;
}

.timeline-dot::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 12px;
  bottom: -30px;
  width: 2px;
  background: #E0E0E0;
  transform: translateX(-50%);
}

.experience-item:last-child .timeline-dot::before,
.education-item:last-child .timeline-dot::before {
  display: none;
}

h4 {
  color: #673AB7;
  margin: 0 0 5px;
  font-size: 1.1em;
}

.company, .degree {
  font-weight: 500;
  margin-bottom: 5px;
}

.period {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 10px;
}

.knowledge {
  color: #666;
  font-style: italic;
  margin-top: 10px;
}

@media print {
  .resume-purple {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
  }
}
</style>
