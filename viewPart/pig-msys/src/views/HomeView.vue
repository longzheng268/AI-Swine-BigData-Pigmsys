<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h2>欢迎使用生猪智慧养殖大数据系统</h2>
          <p>Welcome to AI Swine BigData Management System</p>
        </div>
      </template>

      <div class="content">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover" class="feature-card">
              <div class="feature-icon">
                <el-icon :size="48" color="#409EFF"><TrendCharts /></el-icon>
              </div>
              <h3>生长预测</h3>
              <p>基于日龄、饲料等数据，智能预测生猪生长状况</p>
              <el-button type="primary" @click="goToPrediction">开始预测</el-button>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover" class="feature-card">
              <div class="feature-icon">
                <el-icon :size="48" color="#67C23A"><Histogram /></el-icon>
              </div>
              <h3>环境评价</h3>
              <p>实时监测温度、湿度等环境指标，提供改进建议</p>
              <el-button type="success" @click="goToPrediction">环境评价</el-button>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover" class="feature-card">
              <div class="feature-icon">
                <el-icon :size="48" color="#F56C6C"><Warning /></el-icon>
              </div>
              <h3>疾病风险</h3>
              <p>综合多维度数据，预测疾病风险等级及防控建议</p>
              <el-button type="danger" @click="goToPrediction">风险评估</el-button>
            </el-card>
          </el-col>
        </el-row>

        <el-divider />

        <div class="info-section">
          <h3>系统特点</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <el-icon color="#409EFF"><CircleCheck /></el-icon>
                <span>前端算法集成 - JavaScript 实现，无需 Python 服务依赖</span>
              </div>
              <div class="info-item">
                <el-icon color="#409EFF"><CircleCheck /></el-icon>
                <span>实时预测 - 毫秒级响应速度</span>
              </div>
              <div class="info-item">
                <el-icon color="#409EFF"><CircleCheck /></el-icon>
                <span>数据可视化 - 直观的结果展示</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <el-icon color="#67C23A"><CircleCheck /></el-icon>
                <span>智能建议 - 基于数据分析的专业建议</span>
              </div>
              <div class="info-item">
                <el-icon color="#67C23A"><CircleCheck /></el-icon>
                <span>多维分析 - 生长、环境、疾病全方位监控</span>
              </div>
              <div class="info-item">
                <el-icon color="#67C23A"><CircleCheck /></el-icon>
                <span>易于扩展 - 模块化设计，便于功能扩展</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <div class="tech-stack">
          <h3>技术栈</h3>
          <el-tag>Vue 3</el-tag>
          <el-tag type="success">Element Plus</el-tag>
          <el-tag type="warning">TypeScript</el-tag>
          <el-tag type="danger">Axios</el-tag>
          <el-tag type="info">ECharts</el-tag>
          <el-tag>Spring Boot</el-tag>
          <el-tag type="success">Hadoop</el-tag>
          <el-tag type="warning">MySQL</el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { TrendCharts, Histogram, Warning, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()

const goToPrediction = () => {
  router.push('/prediction')
}
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0 0 5px 0;
  font-size: 28px;
  color: #333;
}

.card-header p {
  margin: 0;
  font-size: 16px;
  color: #666;
}

.content {
  padding: 20px 0;
}

.feature-card {
  text-align: center;
  padding: 20px;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  margin-bottom: 15px;
}

.feature-card h3 {
  margin: 10px 0;
  font-size: 20px;
  color: #333;
}

.feature-card p {
  margin-bottom: 15px;
  color: #666;
  line-height: 1.6;
}

.info-section {
  margin: 30px 0;
}

.info-section h3 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #333;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.info-item .el-icon {
  margin-right: 10px;
}

.tech-stack {
  text-align: center;
}

.tech-stack h3 {
  margin-bottom: 15px;
  font-size: 20px;
  color: #333;
}

.tech-stack .el-tag {
  margin: 5px;
  font-size: 14px;
}
</style>
