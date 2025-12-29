<script setup lang="ts">
import { RouterView, useRouter, useRoute } from 'vue-router'
import { ref, computed } from 'vue'
import { clearAuth, isAuthenticated, getUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { Expand, Fold, HomeFilled, TrendCharts, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
const userInfo = computed(() => getUserInfo())
const isCollapse = ref(false)

const handleLogout = () => {
  clearAuth()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<template>
  <div id="app">
    <!-- 主布局容器 -->
    <el-container v-if="!isLoginPage && isAuthenticated()" class="main-container">
      <!-- 顶部Header -->
      <el-header class="main-header">
        <div class="header-content">
          <div class="header-left">
            <el-icon class="collapse-icon" @click="toggleCollapse">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <h1 class="system-title">生猪智慧养殖大数据系统</h1>
          </div>
          <div class="header-right">
            <span class="user-name" v-if="userInfo">{{ userInfo.name || '用户' }}</span>
            <el-button type="primary" size="default" @click="handleLogout">退出登录</el-button>
          </div>
        </div>
      </el-header>

      <el-container class="content-container">
        <!-- 左侧导航栏 -->
        <el-aside :width="isCollapse ? '64px' : '200px'" class="main-aside">
          <el-menu
            :default-active="route.path"
            :collapse="isCollapse"
            :collapse-transition="false"
            router
            class="sidebar-menu"
          >
            <el-menu-item index="/">
              <el-icon><HomeFilled /></el-icon>
              <template #title>首页</template>
            </el-menu-item>
            <el-menu-item index="/prediction">
              <el-icon><TrendCharts /></el-icon>
              <template #title>智能预测</template>
            </el-menu-item>
            <el-menu-item index="/about">
              <el-icon><InfoFilled /></el-icon>
              <template #title>关于系统</template>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主内容区域 -->
        <el-main class="main-content">
          <RouterView />
        </el-main>
      </el-container>
    </el-container>

    <!-- 登录页面不显示导航栏 -->
    <RouterView v-else />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.main-container {
  height: 100vh;
  width: 100%;
}

/* 顶部Header样式 */
.main-header {
  background: linear-gradient(135deg, #409EFF 0%, #3a8ee6 100%);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px !important;
  display: flex;
  align-items: center;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-icon {
  font-size: 24px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.collapse-icon:hover {
  color: #e6f7ff;
}

.system-title {
  font-size: 20px;
  color: #fff;
  margin: 0;
  font-weight: 500;
  white-space: nowrap;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-name {
  color: #fff;
  font-size: 14px;
}

/* 内容容器 */
.content-container {
  height: calc(100vh - 60px);
}

/* 左侧边栏样式 */
.main-aside {
  background-color: #001529;
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.sidebar-menu {
  border-right: none;
  background-color: #001529;
  height: 100%;
}

.sidebar-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #1890ff;
  color: #fff;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #1890ff;
  color: #fff;
}

.sidebar-menu .el-icon {
  color: rgba(255, 255, 255, 0.85);
  font-size: 18px;
}

.sidebar-menu .el-menu-item.is-active .el-icon {
  color: #fff;
}

/* 主内容区域 */
.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  height: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .system-title {
    font-size: 16px;
  }

  .main-aside {
    position: absolute;
    z-index: 99;
    height: calc(100vh - 60px);
  }

  .main-content {
    margin-left: 0 !important;
  }

  .user-name {
    display: none;
  }
}

@media (max-width: 480px) {
  .system-title {
    font-size: 14px;
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .main-content {
    padding: 10px;
  }
}
</style>
