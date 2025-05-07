<template>
  <div class="resume">
    <div class="banner">
      <div class="banner__fullname">
        {{ person.name.first }} {{ person.name.middle }} {{ person.name.last }}
      </div>
      <div class="banner__position">{{ person.position }}</div>
      <div v-if="person.birth" class="banner__location">
        {{ lang.born }} {{ person.birth.year }} {{ lang.bornIn }}
        {{ person.birth.location }}
      </div>
    </div>

    <div class="content">
      <div class="content__left">
        <div class="section">
          <div class="section-headline">
            {{ lang.about }}
          </div>

          <div class="section-content section-content--plain">
            {{ person.about }}
            <br />
            <br />
            {{ person.knowledge }}
          </div>
        </div>

        <div v-if="person.skills" class="section">
          <div class="section-headline">
            {{ lang.skills }}
          </div>

          <div class="section-content-grid">
            <div v-for="skill in person.skills" :key="skill.name" class="skill-item">
              <div class="skill-name">{{ skill.name }}</div>
              <div class="skill-level">
                <div class="skill-level-bar" :style="{ width: skill.level + '%' }"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="section">
          <div class="section-headline">
            {{ lang.contact }}
          </div>

          <div class="section-content section-content--plain">
            <div class="contact-item">
              <i class="material-icons">location_on</i>
              {{ person.contact.street }}
            </div>
            <div class="contact-item">
              <i class="material-icons">phone</i>
              {{ person.contact.phone }}
            </div>
            <div class="contact-item">
              <i class="material-icons">email</i>
              {{ person.contact.email }}
            </div>
            <div v-if="person.contact.website" class="contact-item">
              <i class="material-icons">language</i>
              {{ person.contact.website }}
            </div>
            <div v-if="person.contact.github" class="contact-item">
              <i class="fa fa-github"></i>
              {{ person.contact.github }}
            </div>
          </div>
        </div>
      </div>

      <div class="content__right">
        <div class="section">
          <div class="section-headline">
            <i class="material-icons">work</i>
            {{ lang.experience }}
          </div>

          <div class="section-content">
            <div v-for="exp in person.experience" :key="exp.company" class="experience-item">
              <div class="experience-title">{{ exp.position }}</div>
              <div class="experience-company">{{ exp.company }}</div>
              <div class="experience-period">{{ exp.timeperiod }}</div>
              <div class="experience-description">{{ exp.description }}</div>
            </div>
          </div>
        </div>

        <div class="section">
          <div class="section-headline">
            <i class="material-icons">school</i>
            {{ lang.education }}
          </div>

          <div class="section-content">
            <div v-for="edu in person.education" :key="edu.school" class="education-item">
              <div class="education-school">{{ edu.school }}</div>
              <div class="education-degree">{{ edu.degree }}</div>
              <div class="education-period">{{ edu.timeperiod }}</div>
              <div class="education-description">{{ edu.description }}</div>
            </div>
          </div>
        </div>

        <div v-if="person.projects" class="section">
          <div class="section-headline">
            <i class="section-headline__icon material-icons">code</i
            >{{ lang.projects }}
          </div>

          <div class="section-content-grid">
            <a
              v-for="(project, index) in person.projects"
              :key="index"
              class="section-content__item-grid"
              :class="{ link: project.url !== undefined }"
              :href="project.url"
            >
              <span class="section-content__header"> {{ project.name }} </span>
              <span class="section-content__subheader">{{
                project.platform
              }}</span>
              <span class="section-content__text">
                {{ project.description }}
              </span>
            </a>
          </div>
        </div>

        <div v-if="person.contributions" class="section">
          <div class="section-headline">
            <i class="section-headline__icon fa fa-heart"></i
            >{{ lang.contributions }}
          </div>

          <div class="section-content-grid">
            <a
              v-for="(contribution, index) in person.contributions"
              class="section-content__item-grid"
              :key="index"
              :class="{ link: contribution.url !== undefined }"
              :href="contribution.url"
            >
              <span class="section-content__header">
                {{ contribution.name }}
              </span>
              <span class="section-content__text">
                {{ contribution.description }}
              </span>
              <span
                class="section-content__text--light"
                style="word-break: break-all"
              >
                {{ contribution.url }}
              </span>
            </a>
          </div>
        </div>
      </div>
    </div>

    <img class="picture" />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
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

<style lang="scss" scoped>
.resume {
  font-family: 'Roboto', sans-serif;
  line-height: 1.5;
  color: #333;
}

.banner {
  background: #42b883;
  color: white;
  padding: 2rem;
  text-align: center;

  &__fullname {
    font-size: 2.5rem;
    font-weight: 300;
    margin-bottom: 0.5rem;
  }

  &__position {
    font-size: 1.5rem;
    margin-bottom: 0.5rem;
  }

  &__location {
    font-size: 1rem;
  }
}

.content {
  display: flex;
  padding: 2rem;
  gap: 2rem;

  &__left {
    flex: 1;
  }

  &__right {
    flex: 2;
  }
}

.section {
  margin-bottom: 2rem;

  &-headline {
    font-size: 1.2rem;
    font-weight: 500;
    color: #42b883;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
}

.skill-item {
  margin-bottom: 1rem;
}

.skill-name {
  margin-bottom: 0.3rem;
}

.skill-level {
  height: 8px;
  background: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.skill-level-bar {
  height: 100%;
  background: #42b883;
  border-radius: 4px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.experience-item,
.education-item {
  margin-bottom: 1.5rem;
}

.experience-title,
.education-school {
  font-weight: 500;
  font-size: 1.1rem;
  margin-bottom: 0.3rem;
}

.experience-company,
.education-degree {
  color: #42b883;
  margin-bottom: 0.3rem;
}

.experience-period,
.education-period {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.3rem;
}

.experience-description,
.education-description {
  color: #666;
  font-size: 0.9rem;
}
</style>
