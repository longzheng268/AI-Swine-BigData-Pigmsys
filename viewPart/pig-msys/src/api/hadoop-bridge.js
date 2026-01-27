/**
 * Hadoop MapReduce 任务提交桥接模块
 * 用于从 Node.js 触发 Hadoop Streaming 任务（fire-and-forget）
 */
const { exec } = require('child_process');
const path = require('path');

/**
 * 提交 MapReduce 任务（异步非阻塞）
 * @param {Object} options - 任务配置选项
 * @param {string} options.jobName - 任务名称
 * @param {string} options.input - HDFS 输入路径
 * @param {string} options.output - HDFS 输出路径
 * @param {string} options.mapper - Mapper 脚本路径
 * @param {string} options.reducer - Reducer 脚本路径
 * @returns {number} 子进程 PID
 */
const submitMapReduceJob = (options = {}) => {
  // 从环境变量获取 Hadoop 路径
  const HADOOP_HOME = process.env.HADOOP_HOME || '/opt/homebrew/opt/hadoop/libexec';
  
  // 构造 hadoop-streaming jar 路径（使用通配符）
  const streamingJarPattern = `${HADOOP_HOME}/share/hadoop/tools/lib/hadoop-streaming-*.jar`;
  
  // 默认参数
  const jobName = options.jobName || 'pig-environment-analysis';
  const inputPath = options.input || '/user/hadoop/pig_data/environment';
  const outputPath = options.output || `/user/hadoop/pig_output/analysis_${Date.now()}`;
  const mapper = options.mapper || 'cat'; // 默认使用 cat（直接输出）
  const reducer = options.reducer || 'wc -l'; // 默认使用 wc 统计行数
  
  // 构造完整的 Hadoop Streaming 命令
  // 注意：使用 sh -c 以支持通配符展开
  const command = `
    export HADOOP_HOME=${HADOOP_HOME} && \
    JAR_PATH=$(ls ${streamingJarPattern} 2>/dev/null | head -n1) && \
    if [ -z "$JAR_PATH" ]; then
      echo "错误: 找不到 hadoop-streaming jar 文件" >&2
      exit 1
    fi && \
    hadoop jar "$JAR_PATH" \\
      -D mapreduce.job.name="${jobName}" \\
      -input "${inputPath}" \\
      -output "${outputPath}" \\
      -mapper "${mapper}" \\
      -reducer "${reducer}"
  `.trim();

  console.log(`🚀 [Hadoop Bridge] 正在提交 MapReduce 任务...`);
  console.log(`   - 任务名称: ${jobName}`);
  console.log(`   - 输入路径: ${inputPath}`);
  console.log(`   - 输出路径: ${outputPath}`);
  console.log(`   - Mapper: ${mapper}`);
  console.log(`   - Reducer: ${reducer}`);

  // 异步执行命令（fire-and-forget）
  const child = exec(command, {
    env: { ...process.env, HADOOP_HOME },
    maxBuffer: 1024 * 1024 * 10, // 10MB buffer
    shell: '/bin/bash' // 明确使用 bash
  });

  const pid = child.pid;
  console.log(`✅ [Hadoop Bridge] 任务已提交，进程 PID: ${pid}`);

  // 监听输出（可选，用于调试）
  child.stdout.on('data', (data) => {
    console.log(`[Hadoop STDOUT]: ${data.toString().trim()}`);
  });

  child.stderr.on('data', (data) => {
    console.error(`[Hadoop STDERR]: ${data.toString().trim()}`);
  });

  child.on('exit', (code, signal) => {
    if (code === 0) {
      console.log(`✅ [Hadoop Bridge] 任务执行完成 (PID: ${pid})`);
    } else {
      console.error(`❌ [Hadoop Bridge] 任务执行失败 (PID: ${pid}, 退出码: ${code}, 信号: ${signal})`);
    }
  });

  child.on('error', (err) => {
    console.error(`❌ [Hadoop Bridge] 任务执行异常 (PID: ${pid}):`, err.message);
  });

  // 立即返回进程 ID（不等待任务完成）
  return pid;
};

/**
 * 预定义的任务模板：环境数据分析
 */
const submitEnvironmentAnalysisJob = () => {
  return submitMapReduceJob({
    jobName: 'pig-environment-analysis',
    input: '/user/hadoop/pig_data/environment',
    output: `/user/hadoop/pig_output/env_analysis_${Date.now()}`,
    mapper: 'cat', // 可替换为自定义 mapper 脚本
    reducer: 'wc -l'
  });
};

/**
 * 预定义的任务模板：猪类型统计
 */
const submitPigTypeStatsJob = () => {
  return submitMapReduceJob({
    jobName: 'pig-type-statistics',
    input: '/user/hadoop/pig_data/pig_info',
    output: `/user/hadoop/pig_output/type_stats_${Date.now()}`,
    mapper: 'cut -f2', // 提取第二列（假设是猪类型）
    reducer: 'sort | uniq -c'
  });
};

module.exports = {
  submitMapReduceJob,
  submitEnvironmentAnalysisJob,
  submitPigTypeStatsJob
};
