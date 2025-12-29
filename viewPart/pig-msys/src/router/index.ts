import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '@/utils/auth'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/prediction',
      name: 'prediction',
      component: () => import('../views/prediction/PredictionView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: false }
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth !== false

  if (requiresAuth && !isAuthenticated()) {
    // 需要认证但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && isAuthenticated()) {
    // 已登录用户访问登录页，跳转到首页
    next('/')
  } else {
    next()
  }
})

export default router
