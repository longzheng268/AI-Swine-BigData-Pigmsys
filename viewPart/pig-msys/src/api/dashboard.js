/**
 * 大屏数据API接口
 */
import myaxios from "@/utils/myaxios";

/**
 * 获取大屏综合数据
 * 注意：此接口需要聚合Hadoop、Python等多个数据源，可能需要较长时间
 */
export function getDashboardData() {
  // 设置 10 秒超时，避免大屏卡住
  return myaxios.get("/api/dashboard/data", {
    timeout: 10000, // 10 秒超时
  });
}

/**
 * 获取实时概览数据
 */
export function getOverview() {
  return myaxios.get("/api/dashboard/overview");
}

/**
 * 获取Hadoop分析结果
 */
export function getHadoopAnalysis() {
  return myaxios.get("/api/dashboard/hadoop-analysis", {
    timeout: 60000, // Hadoop分析可能需要较长时间
  });
}

/**
 * 获取Python预测结果
 */
export function getPrediction() {
  return myaxios.get("/api/dashboard/prediction", {
    timeout: 45000, // Python预测可能需要较长时间
  });
}

/**
 * 健康检查
 */
export function healthCheck() {
  return myaxios.get("/api/dashboard/health");
}

/**
 * 提交 Hadoop MapReduce 任务（fire-and-forget）
 * 调用内置的 Node.js 后端服务
 */
export function submitHadoopJob() {
  // 使用原生 fetch 避免 myaxios 的 baseURL 干扰
  const hostname = window.location.hostname;
  const url = `http://${hostname}:3100/api/hadoop/submit-environment-analysis`;
  
  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({})
  }).then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  });
}
