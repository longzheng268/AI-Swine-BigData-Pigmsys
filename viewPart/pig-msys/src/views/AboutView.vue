<template>
  <div class="about-container">
    <el-card class="about-card">
      <template #header>
        <div class="card-header">
          <h2>关于系统</h2>
          <p>About AI Swine BigData System</p>
        </div>
      </template>

      <div class="content">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <h3>系统简介</h3>
            <p class="intro-text">
              生猪智慧养殖大数据系统是一个基于 Vue 3 和 Spring Boot 的现代化智能养殖管理平台。
              系统集成了先进的机器学习算法，能够为养殖户提供精准的生长预测、环境评价和疾病风险分析服务。
            </p>
          </el-col>
          
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <h3>核心功能</h3>
            <ul class="feature-list">
              <li>智能生长预测算法</li>
              <li>环境质量实时监测与评价</li>
              <li>疾病风险预警系统</li>
              <li>数据可视化分析</li>
              <li>历史数据管理</li>
            </ul>
          </el-col>
        </el-row>

        <el-divider />

        <div class="tech-section">
          <h3>技术架构</h3>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <el-card shadow="hover" class="tech-card">
                <h4>前端技术</h4>
                <ul>
                  <li>Vue 3 (Composition API)</li>
                  <li>TypeScript</li>
                  <li>Element Plus</li>
                  <li>ECharts</li>
                  <li>Vite</li>
                </ul>
              </el-card>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <el-card shadow="hover" class="tech-card">
                <h4>后端技术</h4>
                <ul>
                  <li>Spring Boot</li>
                  <li>MyBatis</li>
                  <li>MySQL</li>
                  <li>Redis</li>
                  <li>JWT 认证</li>
                </ul>
              </el-card>
            </el-col>
            
            <el-col :xs="24" :sm="12" :md="8" :lg="8">
              <el-card shadow="hover" class="tech-card">
                <h4>大数据技术</h4>
                <ul>
                  <li>Hadoop HDFS</li>
                  <li>MapReduce</li>
                  <li>数据分析算法</li>
                  <li>机器学习模型</li>
                </ul>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <div class="version-info">
          <h3>版本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="发布日期">2025-12-29</el-descriptions-item>
            <el-descriptions-item label="开发团队">AI Swine Team</el-descriptions-item>
            <el-descriptions-item label="技术支持">longzheng268@github</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// No reactive state needed for this static page
</script>

<style scoped>
.about-container {
  padding: 0;
  max-width: 100%;
}

.about-card {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
  font-style: italic;
}

.content {
  padding: 20px 0;
}

h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 600;
}

.intro-text {
  line-height: 1.8;
  color: #666;
  text-align: justify;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  padding: 8px 0;
  color: #666;
  position: relative;
  padding-left: 20px;
}

.feature-list li::before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #409EFF;
  font-weight: bold;
}

.tech-section {
  margin: 30px 0;
}

.tech-card {
  margin-bottom: 20px;
  height: 100%;
}

.tech-card h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #409EFF;
  font-weight: 600;
}

.tech-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tech-card li {
  padding: 5px 0;
  color: #666;
  font-size: 14px;
}

.version-info {
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header h2 {
    font-size: 20px;
  }

  h3 {
    font-size: 18px;
  }

  .tech-card {
    margin-bottom: 15px;
  }
}
</style>
