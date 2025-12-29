<template>
  <div class="navbar">
    <!--:router="true" 默认是false 当开启路由时 index的含义变了,原来是代表点哪里哪里亮,现在代表路由-->
    <el-menu
      :router="true"
      default-active="/home"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      background-color="#0093E9"
      background-image="linear-gradient(160deg, #0093E9 0%, #80D0C7 100%)"
      text-color="#fff"
      active-text-color="#ffd04b"
    >
      <el-menu-item index="/home">
        <el-icon><House /></el-icon>
        <template #title>首页</template>
      </el-menu-item>
      <el-menu-item
        index="/big-screen"
        style="
          background: linear-gradient(
            135deg,
            #667eea 0%,
            #764ba2 100%
          ) !important;
        "
      >
        <el-icon><FullScreen /></el-icon>
        <template #title>🎯 可视化大屏</template>
      </el-menu-item>
      <el-sub-menu index="2">
        <template #title>
          <el-icon><Platform /></el-icon>
          <span>生猪信息管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="/pigType">查看猪状态(关于我们)</el-menu-item>
          <el-menu-item index="/pigInfo">生猪信息管理</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <el-sub-menu index="3">
        <template #title>
          <el-icon><Reading /></el-icon>
          <span>猪的生产周期管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="/birthDetail">生产分析</el-menu-item>
          <el-menu-item index="/purchaseDetail">买入数量</el-menu-item>
          <el-menu-item index="/tradeDetail">交易数量</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <el-sub-menu index="4">
        <template #title>
          <el-icon><DataLine /></el-icon>
          <span>环境监测管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="/environment">环境监测数据</el-menu-item>
          <el-menu-item index="/visualization">数据可视化</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <!-- 智能分析预测菜单 - 仅管理员和科研人员可见 -->
      <!-- 如需让所有用户可见，可移除 v-if 条件 -->
      <el-sub-menu index="5" v-if="userRole === 1 || userRole === 3">
        <template #title>
          <el-icon><Cpu /></el-icon>
          <span>智能分析预测</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="/prediction">预测分析</el-menu-item>
          <el-menu-item index="/comparison">方案对比</el-menu-item>
          <el-menu-item index="/upload">数据导入</el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
      <el-menu-item index="/user" v-if="userRole === 1">
        <el-icon><User /></el-icon>
        <template #title>用户信息管理</template>
      </el-menu-item>
      <el-menu-item index="/personal">
        <el-icon><UserFilled /></el-icon>
        <template #title>个人信息管理</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
import {
  House,
  FullScreen,
  Platform,
  Reading,
  DataLine,
  Cpu,
  User,
  UserFilled,
} from "@element-plus/icons-vue";
import { ElMenu, ElMenuItem, ElSubMenu, ElMenuItemGroup } from "element-plus";

export default {
  name: "index",
  components: {
    House,
    FullScreen,
    Platform,
    Reading,
    DataLine,
    Cpu,
    User,
    UserFilled,
    // 显式注册菜单组件，确保正确解析（同时注册 PascalCase 和 kebab-case）
    ElMenu,
    ElMenuItem,
    ElSubMenu,
    ElMenuItemGroup,
    // Vue 3 中使用 kebab-case 注册，确保模板中的 kebab-case 也能识别
    "el-menu": ElMenu,
    "el-menu-item": ElMenuItem,
    "el-sub-menu": ElSubMenu, // Element Plus 3.x 中使用 el-sub-menu
    "el-menu-item-group": ElMenuItemGroup,
    // 为了兼容性，也注册旧名称
    "el-submenu": ElSubMenu,
    // 也可以使用 SubMenu 作为别名（某些情况下可能需要）
    SubMenu: ElSubMenu,
    MenuItemGroup: ElMenuItemGroup,
  },
  data() {
    return {
      userRole: null, // 用户角色：1-管理员，2-普通用户，3-科研人员
    };
  },
  created() {
    // 获取用户角色
    const userInfo = localStorage.getItem("my-login-user");
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        this.userRole = user.roleId || 2; // 默认普通用户
        // 开发环境：显示当前用户角色信息
        if (process.env.NODE_ENV === "development") {
          console.log("👤 当前用户角色:", {
            roleId: this.userRole,
            roleName:
              this.userRole === 1
                ? "管理员"
                : this.userRole === 3
                  ? "科研人员"
                  : "普通用户",
            username: user.name || user.username,
            canViewPrediction: this.userRole === 1 || this.userRole === 3,
          });
        }
      } catch (e) {
        console.error("解析用户信息失败:", e);
        this.userRole = 2;
      }
    } else {
      // 开发环境：提示没有用户信息
      if (process.env.NODE_ENV === "development") {
        console.warn("⚠️ 未找到用户信息，无法确定角色");
      }
    }
  },
  methods: {
    handleOpen(key, keyPath) {
      // 调试日志 - 生产环境可移除
      if (process.env.NODE_ENV === "development") {
        console.log("菜单打开:", key, keyPath);
      }
    },
    handleClose(key, keyPath) {
      // 调试日志 - 生产环境可移除
      if (process.env.NODE_ENV === "development") {
        console.log("菜单关闭:", key, keyPath);
      }
    },
    // 检查菜单是否可见（根据角色）
    canViewMenu(requiredRole) {
      if (!requiredRole) return true; // 没有角色要求，所有人可见
      if (this.userRole === 1) return true; // 管理员可以看到所有菜单
      return this.userRole === requiredRole;
    },
  },
};
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>
