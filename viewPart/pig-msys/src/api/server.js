/**
 * 集成在 Vue 中的 Node.js 后端服务
 * 在 main.js 中启动，处理 Hadoop MapReduce 任务提交
 */
const express = require('express');
const cors = require('cors');
const { exec } = require('child_process');

const app = express();
const PORT = process.env.HADOOP_BRIDGE_PORT || 3100;

// 中间件
app.use(cors());
app.use(express.json());

// CORS preflight 请求处理
app.options('*', cors());

// 请求日志
app.use((req, res, next) => {
  console.log(`📨 [${new Date().toISOString()}] ${req.method} ${req.path}`);
  next();
});

/**
 * 根路由（健康检查）
 */
app.get('/', (req, res) => {
  res.json({
    status: 'ok',
    service: 'hadoop-bridge-running',
    timestamp: new Date().toISOString(),
    port: PORT
  });
});

/**
 * 健康检查
 */
app.get('/health', (req, res) => {
  res.json({
    status: 'ok',
    service: 'hadoop-bridge',
    timestamp: new Date().toISOString()
  });
});

/**
 * 提交环境数据分析任务
 */
app.post('/api/hadoop/submit-environment-analysis', (req, res) => {
  try {
    console.log('✅ 收到环境分析任务请求');
    const HADOOP_HOME = process.env.HADOOP_HOME || '/opt/homebrew/opt/hadoop/libexec';
    const jobName = 'pig-environment-analysis';
    const inputPath = '/pig-system/dashboard';  // 修改为实际存在的路径
    const outputPath = `/pig-system/output/env_analysis_${Date.now()}`;  // 输出到 output 目录

    console.log(`🚀 [Hadoop Bridge] 正在提交 MapReduce 任务: ${jobName}`);
    console.log(`   📂 输入: ${inputPath}`);
    console.log(`   📂 输出: ${outputPath}`);

    // 异步执行（fire-and-forget）
    const command = `
      export HADOOP_HOME=${HADOOP_HOME} && \
      JAR_PATH=$(ls ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-*.jar 2>/dev/null | head -n1) && \
      if [ -z "$JAR_PATH" ]; then
        echo "错误: 找不到 hadoop-streaming jar" >&2
        exit 1
      fi && \
      hadoop jar "$JAR_PATH" \
        -D mapreduce.job.name="${jobName}" \
        -input "${inputPath}" \
        -output "${outputPath}" \
        -mapper "cat" \
        -reducer "wc -l"
    `.trim();

    const child = exec(command, {
      env: { ...process.env, HADOOP_HOME },
      maxBuffer: 1024 * 1024 * 10,
      shell: '/bin/bash'
    });

    const pid = child.pid;
    console.log(`✅ 任务已提交，PID: ${pid}`);

    // 立即返回
    console.log('✅ 返回响应给前端');
    res.json({
      code: 200,
      message: '环境分析任务已提交',
      data: {
        pid,
        jobName,
        submittedAt: new Date().toISOString()
      }
    });

    // 监听输出
    child.stdout.on('data', (data) => {
      console.log(`[Hadoop STDOUT]: ${data.toString().trim()}`);
    });

    child.stderr.on('data', (data) => {
      console.error(`[Hadoop STDERR]: ${data.toString().trim()}`);
    });

    child.on('exit', (code) => {
      code === 0 
        ? console.log(`✅ 任务完成 (PID: ${pid})`)
        : console.error(`❌ 任务失败 (PID: ${pid}, 退出码: ${code})`);
    });

    // 立即返回
    res.json({
      code: 200,
      message: '环境分析任务已提交',
      data: {
        pid,
        jobName,
        submittedAt: new Date().toISOString()
      }
    });
  } catch (error) {
    console.error('❌ 提交任务失败:', error.message);
    res.status(500).json({
      code: 500,
      message: '任务提交失败',
      error: error.message
    });
  }
});

/**
 * 提交猪类型统计任务
 */
app.post('/api/hadoop/submit-pig-type-stats', (req, res) => {
  try {
    const HADOOP_HOME = process.env.HADOOP_HOME || '/opt/homebrew/opt/hadoop/libexec';
    const jobName = 'pig-type-statistics';
    const inputPath = '/pig-system/dashboard';  // 修改为实际存在的路径
    const outputPath = `/pig-system/output/type_stats_${Date.now()}`;  // 输出到 output 目录

    console.log(`🚀 [Hadoop Bridge] 正在提交 MapReduce 任务: ${jobName}`);

    const command = `
      export HADOOP_HOME=${HADOOP_HOME} && \
      JAR_PATH=$(ls ${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-*.jar 2>/dev/null | head -n1) && \
      hadoop jar "$JAR_PATH" \
        -D mapreduce.job.name="${jobName}" \
        -input "${inputPath}" \
        -output "${outputPath}" \
        -mapper "cut -f2" \
        -reducer "sort | uniq -c"
    `.trim();

    const child = exec(command, {
      env: { ...process.env, HADOOP_HOME },
      maxBuffer: 1024 * 1024 * 10,
      shell: '/bin/bash'
    });

    console.log(`✅ 任务已提交，PID: ${child.pid}`);

    res.json({
      code: 200,
      message: '猪类型统计任务已提交',
      data: {
        pid: child.pid,
        jobName,
        submittedAt: new Date().toISOString()
      }
    });
  } catch (error) {
    console.error('❌ 提交任务失败:', error.message);
    res.status(500).json({
      code: 500,
      message: '任务提交失败',
      error: error.message
    });
  }
});

// 错误处理
app.use((err, req, res, next) => {
  console.error('❌ 服务器错误:', err.message);
  res.status(500).json({
    code: 500,
    message: '服务器内部错误',
    error: process.env.NODE_ENV === 'development' ? err.message : '服务异常'
  });
});

// 启动服务器（如果直接运行此文件）
if (require.main === module) {
  const server = app.listen(PORT, '0.0.0.0', () => {
    console.log('🚀 ========================================');
    console.log(`🚀 Hadoop Bridge 服务已启动`);
    console.log(`🚀 监听端口: ${PORT}`);
    console.log(`🚀 监听地址: 0.0.0.0 (所有网络接口)`);
    console.log(`🚀 HADOOP_HOME: ${process.env.HADOOP_HOME || '未设置'}`);
    console.log('🚀 ========================================');
  });

  server.on('error', (err) => {
    if (err.code === 'EADDRINUSE') {
      console.error(`❌ 端口 ${PORT} 被占用`);
      process.exit(1);
    }
  });
}

module.exports = app;
