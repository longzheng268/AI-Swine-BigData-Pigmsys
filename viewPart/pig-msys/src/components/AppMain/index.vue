<template>
  <div class="main">
    <el-breadcrumb separator="/" v-show="$route.path!='/home'">
<!--      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>-->
      <span class="line">|</span>
      <el-breadcrumb-item v-show="$route.path=='/pigInfo' || $route.path=='/pigType'">生猪信息管理</el-breadcrumb-item>
      <el-breadcrumb-item v-show="$route.path=='/birthDetail' || $route.path=='/purchaseDetail' || $route.path=='/tradeDetail'">生猪种类管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="$route.path">{{$route.meta.title}}</el-breadcrumb-item>
    </el-breadcrumb>
    <!--路由出口-->
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "index"
}
</script>

<style scoped>
.el-breadcrumb{
  height: 10px;
  padding: 15px;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}
.line{
  position: absolute;
  color: white;
  border-left: 3px solid red;
  left: 10px;
}
</style>
