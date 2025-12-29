<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>生猪智慧养殖大数据系统</h2>
        <p>AI Swine BigData System</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="userpassword">
          <el-input
            v-model="loginForm.userpassword"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p>默认账号: admin / 123456789</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getUserInfo } from '@/api/auth'
import { setToken, setUserInfo } from '@/utils/auth'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  userpassword: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  userpassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 登录请求
        const response = await login({
          username: loginForm.username,
          userpassword: loginForm.userpassword
        })

        if (response.code === 200 && response.data?.token) {
          const token = response.data.token
          
          // 存储 token
          setToken(token)
          
          // 获取用户信息
          try {
            const userInfoResponse = await getUserInfo(token)
            if (userInfoResponse.code === 200) {
              setUserInfo(userInfoResponse.data)
            }
          } catch (error) {
            console.warn('Failed to fetch user info:', error)
          }
          
          ElMessage.success('登录成功')
          
          // 跳转到首页
          router.push('/')
        } else {
          ElMessage.error(response.message || '登录失败')
        }
      } catch (error: any) {
        console.error('Login error:', error)
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
}

.login-footer p {
  margin: 0;
  color: #999;
  font-size: 12px;
}
</style>
