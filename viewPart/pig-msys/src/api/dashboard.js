/**
 * 大屏数据API接口
 */
import myaxios from '@/utils/myaxios'

/**
 * 获取大屏综合数据
 * 注意：此接口需要聚合Hadoop、Python等多个数据源，可能需要较长时间
 */
export function getDashboardData() {
  // 创建临时axios实例，设置更长的超时时间
  return myaxios.get('/api/dashboard/data', {
    timeout: 60000 // 增加到60秒，因为需要聚合多个数据源
  })
}

/**
 * 获取实时概览数据
 */
export function getOverview() {
  return myaxios.get('/api/dashboard/overview')
}

/**
 * 获取Hadoop分析结果
 */
export function getHadoopAnalysis() {
  return myaxios.get('/api/dashboard/hadoop-analysis', {
    timeout: 60000 // Hadoop分析可能需要较长时间
  })
}

/**
 * 获取Python预测结果
 */
export function getPrediction() {
  return myaxios.get('/api/dashboard/prediction', {
    timeout: 45000 // Python预测可能需要较长时间
  })
}

/**
 * 健康检查
 */
export function healthCheck() {
  return myaxios.get('/api/dashboard/health')
}

