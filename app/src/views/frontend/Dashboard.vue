<template>
  <div class="dashboard-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="main-title">智简未来</h1>
        <h2 class="sub-title">智能招聘系统</h2>
        <p class="description">高效智能的招聘解决方案，助力企业精准匹配人才</p>
        <el-button
          type="primary"
          size="large"
          class="get-started-btn"
          @click="$router.push('/jobs')"
        >
          立即开始
        </el-button>
      </div>
      <div class="hero-image">
        <img
          src="@/assets/recruitment-illustration.svg"
          alt="recruitment"
          class="illustration"
        />
      </div>
    </div>

    <div class="features-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="feature-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>简历管理</span>
              </div>
            </template>
            <div class="card-content">
              <p>智能管理您的求职材料</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Briefcase /></el-icon>
                <span>职位匹配</span>
              </div>
            </template>
            <div class="card-content">
              <p>AI智能推荐合适职位</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><ChatDotRound /></el-icon>
                <span>智能助手</span>
              </div>
            </template>
            <div class="card-content">
              <p>24小时在线职业咨询</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
// No imports needed for now since we're not using the icons in the current implementation
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 40px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);

  .hero-section {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 60px;

    .hero-content {
      flex: 1;
      padding-right: 60px;

      .main-title {
        font-size: 3.5rem;
        color: #2c3e50;
        margin-bottom: 1rem;
        font-weight: bold;
      }

      .sub-title {
        font-size: 2rem;
        color: #3498db;
        margin-bottom: 1.5rem;
      }

      .description {
        font-size: 1.2rem;
        color: #606266;
        margin-bottom: 2rem;
        line-height: 1.6;
      }

      .get-started-btn {
        padding: 12px 30px;
        font-size: 1.1rem;
        border-radius: 8px;
        background: linear-gradient(45deg, #409eff, #3498db);
        border: none;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
          transition: all 0.3s ease;
        }
      }
    }

    .hero-image {
      flex: 1;
      display: flex;
      justify-content: center;

      .illustration {
        max-width: 100%;
        height: auto;
      }
    }
  }

  .features-section {
    margin-top: 40px;

    .feature-card {
      height: 100%;
      transition: transform 0.3s ease;
      border-radius: 12px;
      overflow: hidden;

      &:hover {
        transform: translateY(-5px);
      }

      .card-header {
        display: flex;
        align-items: center;
        padding: 20px;
        background: linear-gradient(45deg, #f5f7fa, #e4e7eb);

        .el-icon {
          font-size: 24px;
          margin-right: 12px;
          color: #409eff;
        }

        span {
          font-size: 1.2rem;
          font-weight: 600;
          color: #2c3e50;
        }
      }

      .card-content {
        padding: 20px;

        p {
          color: #606266;
          font-size: 1rem;
          line-height: 1.6;
        }
      }
    }
  }
}
</style> 