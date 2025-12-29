<script setup lang="ts">
import { RouterView, useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'
import { clearAuth, isAuthenticated, getUserInfo } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
const userInfo = computed(() => getUserInfo())

const handleLogout = () => {
  clearAuth()
  ElMessage.success('退出登录成功')
  router.push('/login')
}
</script>

<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <el-container v-if="!isLoginPage && isAuthenticated()">
      <el-header style="padding: 0;">
        <div class="header-container">
          <div class="logo">
            <h1>生猪智慧养殖大数据系统</h1>
          </div>
          <el-menu
            mode="horizontal"
            :default-active="route.path"
            router
            class="nav-menu"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/prediction">智能预测</el-menu-item>
            <el-menu-item index="/about">关于</el-menu-item>
          </el-menu>
          <div class="user-info">
            <span v-if="userInfo">{{ userInfo.name || '用户' }}</span>
            <el-button type="text" @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <RouterView />
      </el-main>
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

.el-container {
  height: 100%;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.header-container {
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
}

.logo h1 {
  font-size: 18px;
  color: #333;
  margin: 0;
  white-space: nowrap;
  margin-right: 30px;
}

.nav-menu {
  flex: 1;
  border: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info span {
  color: #666;
  font-size: 14px;
}

.el-main {
  background-color: #f5f7fa;
  padding: 0;
  overflow-y: auto;
}
</style>
