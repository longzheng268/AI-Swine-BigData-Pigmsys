import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/login/index.vue";
import Layout from "../components/Layout";
import home from "../views/home/index.vue";
import pigType from "../views/pig/PigType";
import pigInfo from "../views/pig/PigInfo";
import BirthDetail from "../views/detail/BirthDetail";
import PurchaseDetail from "../views/detail/PurchaseDetail";
import TradeDetail from "../views/detail/TradeDetail";
import user from "../views/user/user";
import InfoPerson from "../views/personal/InfoPerson";
import EnvironmentData from "../views/environment/EnvironmentData";
import DataUpload from "../views/upload/DataUpload";
import PredictionAnalysis from "../views/prediction/PredictionAnalysis";
import ComparisonAnalysis from "../views/prediction/ComparisonAnalysis";
import DataVisualization from "../views/analysis/DataVisualization";
import BigScreen from "../views/dashboard/BigScreen";

const routes = [
  {
    path: "/login",
    name: "login",
    component: Login,
  },
  {
    path: "/big-screen",
    name: "BigScreen",
    component: BigScreen,
    meta: { title: "可视化大屏", fullscreen: true },
  },
  {
    path: "/",
    name: "Layout",
    component: Layout,
    redirect: "/home",
    children: [
      {
        path: "/home",
        name: "home",
        component: home,
        meta: { title: "首页" },
      },
      {
        path: "/pigType",
        name: "pigType",
        component: pigType,
        meta: { title: "信息化服务" },
      },
      {
        path: "/pigInfo",
        name: "pigInfo",
        component: pigInfo,
        meta: { title: "猪的信息管理" },
      },
      {
        path: "/birthDetail",
        name: "BirthDetail",
        component: BirthDetail,
        meta: { title: "生产分析" },
      },
      {
        path: "/purchaseDetail",
        name: "PurchaseDetail",
        component: PurchaseDetail,
        meta: { title: "买入数量" },
      },
      {
        path: "/tradeDetail",
        name: "TradeDetail",
        component: TradeDetail,
        meta: { title: "交易数量" },
      },
      {
        path: "/user",
        name: "user",
        component: user,
        meta: { title: "用户信息管理" },
      },
      {
        path: "/personal",
        name: "personal",
        component: InfoPerson,
        meta: { title: "个人信息管理" },
      },
      {
        path: "/environment",
        name: "environment",
        component: EnvironmentData,
        meta: { title: "环境监测数据" },
      },
      {
        path: "/upload",
        name: "upload",
        component: DataUpload,
        meta: { title: "数据导入" },
      },
      {
        path: "/prediction",
        name: "prediction",
        component: PredictionAnalysis,
        meta: { title: "预测分析" },
      },
      {
        path: "/comparison",
        name: "comparison",
        component: ComparisonAnalysis,
        meta: { title: "方案对比" },
      },
      {
        path: "/visualization",
        name: "visualization",
        component: DataVisualization,
        meta: { title: "数据可视化" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
