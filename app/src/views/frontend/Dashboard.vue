<template>
  <div class="dashboard-container">
    <h1>Welcome to Your Dashboard</h1>
    <div class="dashboard-content">
      <el-card class="welcome-card">
        <template #header>
          <div class="card-header">
            <span>欢迎使用智简未来招聘系统</span>
          </div>
        </template>
        <div class="card-content">
          <p>开始探索您的个性化招聘之旅</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
// Component logic will be added here as needed
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;

  .dashboard-content {
    margin-top: 20px;
  }

  .welcome-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .card-content {
      padding: 20px 0;
    }
  }
}
</style> 