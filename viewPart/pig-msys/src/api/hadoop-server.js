/**
 * Hadoop MapReduce 任务提交 API 服务
 * Express 服务器，提供 RESTful API 供前端调用
 */
const express = require('express');
const cors = require('cors');
const { 
  submitMapReduceJob, 
  submitEnvironmentAnalysisJob,
  submitPigTypeStatsJob 
} = require('./hadoop-bridge');

const app = express();
const PORT = process.env.HADOOP_BRIDGE_PORT || 3100;

// 中间件
app.use(cors()); // 允许跨域（前端 Vue 可访问）
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 请求日志中间件
app.use((req, res, next) => {
  console.log(`📨 [${new Date().toISOString()}] ${req.method} ${req.path}`);
  next();
});

/**
 * 健康检查接口
 */
app.get('/health', (req, res) => {
  res.json({
    status: 'ok',
    service: 'hadoop-bridge',
    timestamp: new Date().toISOString(),
    hadoop_home: process.env.HADOOP_HOME || '未设置'
  });
});

/**
 * 通用 MapReduce 任务提交接口
 * POST /api/hadoop/submit
 */
app.post('/api/hadoop/submit', (req, res) => {
  try {
    const { jobName, input, output, mapper, reducer } = req.body;
    
    // 提交任务（fire-and-forget）
    const pid = submitMapReduceJob({
      jobName,
      input,
      output,
      mapper,
      reducer
    });

    // 立即返回成功响应（不等待任务完成）
    res.json({
      code: 200,
      message: 'MapReduce 任务已提交',
      data: {
        pid,
        jobName: jobName || 'default-job',
        submittedAt: new Date().toISOString()
      }
    });
  } catch (error) {
    console.error('❌ 提交任务失败:', error);
    res.status(500).json({
      code: 500,
      message: '任务提交失败',
      error: error.message
    });
  }
});

/**
 * 快速提交：环境数据分析任务
 * POST /api/hadoop/submit-environment-analysis
 */
app.post('/api/hadoop/submit-environment-analysis', (req, res) => {
  try {
    const pid = submitEnvironmentAnalysisJob();
    
    res.json({
      code: 200,
      message: '环境分析任务已提交',
      data: {
        pid,
        jobName: 'pig-environment-analysis',
        submittedAt: new Date().toISOString()
      }
    });
  } catch (error) {
    console.error('❌ 提交环境分析任务失败:', error);
    res.status(500).json({
      code: 500,
      message: '任务提交失败',
      error: error.message
    });
  }
});

/**
 * 快速提交：猪类型统计任务
 * POST /api/hadoop/submit-pig-type-stats
 */
app.post('/api/hadoop/submit-pig-type-stats', (req, res) => {
  try {
    const pid = submitPigTypeStatsJob();
    
    res.json({
      code: 200,
      message: '猪类型统计任务已提交',
      data: {
        pid,
        jobName: 'pig-type-statistics',
        submittedAt: new Date().toISOString()
      }
    });
  } catch (error) {
    console.error('❌ 提交猪类型统计任务失败:', error);
    res.status(500).json({
      code: 500,
      message: '任务提交失败',
      error: error.message
    });
  }
});

/**
 * 404 处理
 */
app.use((req, res) => {
  res.status(404).json({
    code: 404,
    message: '接口不存在',
    path: req.path
  });
});

/**
 * 全局错误处理
 */
app.use((err, req, res, next) => {
  console.error('❌ 服务器错误:', err);
  res.status(500).json({
    code: 500,
    message: '服务器内部错误',
    error: process.env.NODE_ENV === 'development' ? err.message : '服务异常'
  });
});

// 启动服务器
app.listen(PORT, () => {
  console.log('🚀 ========================================');
  console.log(`🚀 Hadoop Bridge API 服务已启动`);
  console.log(`🚀 监听端口: ${PORT}`);
  console.log(`🚀 健康检查: http://localhost:${PORT}/health`);
  console.log(`🚀 HADOOP_HOME: ${process.env.HADOOP_HOME || '⚠️  未设置（将使用默认路径）'}`);
  console.log('🚀 ========================================');
});

// 优雅关闭
process.on('SIGINT', () => {
  console.log('\n⏹️  正在关闭 Hadoop Bridge 服务...');
  process.exit(0);
});

process.on('SIGTERM', () => {
  console.log('\n⏹️  正在关闭 Hadoop Bridge 服务...');
  process.exit(0);
});
