<template>
  <div class="resume">
    <div class="banner">
      <div
        class="banner__fullname"
      >{{ person.name.first }} {{ person.name.middle }} {{ person.name.last }}</div>
      <div class="banner__position">{{ person.position }}</div>
      <div
        class="banner__location"
      >{{ lang.born }} {{person.birth.year}} {{ lang.bornIn }} {{person.birth.location}}</div>
    </div>

    <div class="content">
      <div class="content__left">
        <div class="section">
          <div class="section-headline">{{ lang.about }}</div>

          <div class="section-content section-content--plain">
            {{ person.about }}
            <br>
            <br>
            {{ person.knowledge }}
          </div>
        </div>

        <div
          v-if="person.skills"
          class="section">
          <div class="section-headline">
            {{ lang.skills }}
          </div>

          <div class="section-content-grid">
            <a
              v-for="(skill, index) in person.skills"
              class="grid-item"
              :key="index"
              :href="skill.url">
              <span class="squarred-grid-item">
                {{ skill.name }}
              </span>
            </a>
          </div>
        </div>

        <div class="section">
          <div class="section-headline">
            {{ lang.contact }}
          </div>

          <div class="section-content section-content--plain">
            <div class="section-link">
              <i class="section-link__icon material-icons">business</i>{{ person.contact.street }}
            </div>

            <a
              class="section-link"
              :href="contactLinks.email">
              <i class="section-link__icon material-icons">mail</i>{{ person.contact.email }}
            </a>

            <div class="section-link">
              <i class="section-link__icon material-icons">phone</i>{{ person.contact.phone }}
            </div>

            <a
              v-if="person.contact.website"
              class="section-link"
              :href="person.contact.website">
              <i class="section-link__icon fa fa-globe"></i>{{ person.contact.website }}
            </a>

            <a
              v-if="person.contact.linkedin"
              class="section-link"
              :href="contactLinks.linkedin">
              <i class="section-link__icon fa fa-linkedin"></i>{{ person.contact.linkedin }}
            </a>

            <a
              v-if="person.contact.github"
              class="section-link"
              :href="contactLinks.github">
              <i class="section-link__icon fa fa-github"></i>{{ person.contact.github }}
            </a>

            <a
              v-if="person.contact.medium"
              class="section-link"
              :href="contactLinks.medium">
              <i class="section-link__icon fa fa-medium"></i>{{ person.contact.medium }}
            </a>
          </div>
        </div>
      </div>

      <div class="content__right">
        <div class="section">
          <div class="section-headline">
            <i class="section-headline__icon material-icons">work</i>{{ lang.experience }}
          </div>

          <div class="section-content">
            <a
              v-for="(experience, index) in person.experience"
              :key="index"
              class="section-content__item"
              :href="experience.website">

              <span class="section-content__header">{{ experience.position }}</span>
              <span class="section-content__subheader">
                {{ experience.company }}
                <span class="section-content__plain">{{ experience.location }}</span>
              </span>

              <div class="section-content__text">{{ experience.timeperiod }}</div>
              <span class="section-content__text--light">{{ experience.description }}</span>
            </a>
          </div>
        </div>

        <div class="section">
          <div class="section-headline">
            <i class="section-headline__icon material-icons">school</i>{{ lang.education }}
          </div>

          <div class="section-content">
            <a
              v-for="(education, index) in person.education"
              class="section-content__item"
              :key="index"
              :href="education.website">

              <span class="section-content__header"> {{ education.school }} </span>
              <span class="section-content__subheader">{{ education.degree }}</span>
              <span class="section-content__text"> {{ education.timeperiod }} </span>
              <span class="section-content__text--light"> {{ education.description }} </span>
            </a>
          </div>
        </div>

        <div
          v-if="person.projects"
          class="section">
          <div class="section-headline">
            <i class="section-headline__icon material-icons">code</i>{{ lang.projects }}
          </div>

          <div class="section-content-grid">
            <a v-for="(project, index) in person.projects" :key="index"
              class="section-content__item-grid"
              :href="project.url">
              <span class="section-content__header"> {{ project.name }} </span>
              <span class="section-content__subheader">{{ project.platform }}</span>
              <span class="section-content__text"> {{ project.description }} </span>
            </a>
          </div>
        </div>

        <div
          v-if="person.contributions"
          class="section">
          <div class="section-headline">
            <i class="section-headline__icon fa fa-heart"></i>{{lang.contributions}}
          </div>

          <div class="section-content-grid">
            <a
              v-for="(contribution, index) in person.contributions"
              class="section-content__item-grid"
              :key="index"
              :href="contribution.url">
              <span class="section-content__header"> {{ contribution.name }} </span>
              <span class="section-content__text"> {{ contribution.description }} </span>
              <span class="section-content__text--light" style="word-break: break-all;">
                {{ contribution.url }}
              </span>
            </a>
          </div>
        </div>
      </div>
    </div>

    <img class="picture"/>

  </div>
</template>

<script>
import Vue from 'vue';
import { getVueOptions } from './options';

const name = 'cool-rtl2';
export default Vue.component(name, getVueOptions(name));
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
@accent-color: #34495E;
@banner-color: #42b883;
@banner-height: 120px;
@picture-size: 120px;
@picture-offset: 35px;
@base-padding: 30px;
@left-column-width: 240px;

.resume {
  position: relative;
  font-family: 'Roboto', sans-serif !important;
  font-size: 0.9em;
  direction: rtl;
  width: 100%;
  background: #fff;
}

.picture {
  position: absolute;
  top: @banner-height - @picture-offset;
  right: @base-padding * 2;
  height: @picture-size;
  width: @picture-size;
  border-radius: 50%;
  border: 5px solid @accent-color;
  content: url('../../resume/id.jpg');
  z-index: 2;
}

.banner {
  padding: @base-padding;
  height: @banner-height;
  background-color: @banner-color;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  color: white;

  &__fullname {
    font-size: 32px;
    margin-bottom: 5px;
  }

  &__position {
    font-size: 16px;
    margin-bottom: 5px;
  }

  &__location {
    font-size: 12px;
  }
}

.content {
  display: flex;
  flex-direction: row-reverse;
  padding: @base-padding;
  padding-top: @picture-offset + @base-padding;
  gap: @base-padding;

  &__left {
    width: @left-column-width;
    background-color: @accent-color;
    padding: @base-padding;
    color: white;

    .section-headline {
      color: white;
      border-bottom: 2px solid rgba(255, 255, 255, 0.2);
      padding-bottom: 5px;
    }

    .section-content {
      &--plain {
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .section-link {
      color: rgba(255, 255, 255, 0.8);
      &:hover {
        color: white;
      }
      &__icon {
        color: white;
      }
    }

    .grid-item {
      .squarred-grid-item {
        background-color: rgba(255, 255, 255, 0.1);
        color: white;
        &:hover {
          background-color: rgba(255, 255, 255, 0.2);
        }
      }
    }
  }

  &__right {
    flex: 1;
    padding: @base-padding;
  }
}

.section {
  margin-bottom: @base-padding;

  &-headline {
    display: flex;
    align-items: center;
    font-size: 1.2em;
    font-weight: bold;
    text-transform: uppercase;
    margin-bottom: 15px;

    &__icon {
      margin-left: 10px;
      font-size: 1.4em;
    }
  }

  &-content {
    &-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 10px;
    }

    &--plain {
      padding: 10px 0;
      line-height: 1.5;
    }

    &__item {
      display: block;
      margin-bottom: 20px;
      text-decoration: none;
      color: inherit;

      &:hover {
        color: @accent-color;
      }
    }

    &__item-grid {
      padding: 10px;
      background-color: #f5f5f5;
      border-radius: 5px;
      text-decoration: none;
      color: inherit;

      &:hover {
        background-color: #e8e8e8;
      }
    }

    &__header {
      display: block;
      font-weight: bold;
      margin-bottom: 5px;
    }

    &__subheader {
      display: block;
      font-weight: 500;
      margin-bottom: 5px;
      color: @accent-color;
    }

    &__text {
      display: block;
      margin-bottom: 5px;

      &--light {
        color: #666;
        font-size: 0.9em;
      }
    }
  }
}

.section-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: inherit;
  margin-bottom: 10px;

  &:hover {
    color: @accent-color;
  }

  &__icon {
    margin-left: 10px;
    font-size: 1.2em;
    color: @accent-color;
  }
}

.grid-item {
  text-decoration: none;
  color: inherit;

  .squarred-grid-item {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 10px;
    background-color: #f5f5f5;
    border-radius: 5px;
    min-height: 80px;

    &:hover {
      background-color: #e8e8e8;
    }
  }
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;

    &__left {
      width: 100%;
      margin-bottom: @base-padding;
    }

    &__right {
      padding: 0;
    }
  }

  .picture {
    right: 50%;
    transform: translateX(50%);
  }
}
</style>
