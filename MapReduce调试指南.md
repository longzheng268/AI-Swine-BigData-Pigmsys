# MapReduce 调试指南

## 概述

本文档说明如何调试和验证 Hadoop MapReduce 作业是否正确提交到 YARN 集群。

## 已修复的问题

### 1. 输入路径错误
**问题**: `HadoopService` 的 `runPigDataAnalysis()` 和 `runEnvironmentDataAnalysis()` 方法错误地将 `inputPath` 前缀添加到已经是完整 HDFS 路径的输入文件。

**修复**: 方法现在直接使用传入的完整 HDFS 路径。

### 2. CSV 格式不匹配
**问题**: MapReduce Mapper 期望的 CSV 格式与 `DashboardService` 生成的格式不匹配。

**修复**:
- `PigDataAnalysisMapper`: 现在从字段索引 3 读取猪类型（而不是索引 1）
- `EnvironmentDataAnalysisMapper`: 更新字段索引以匹配实际 CSV 格式

### 3. 日志噪音
**问题**: DEBUG 级别的数据库日志淹没了重要的 MapReduce 信息。

**修复**: 
- 将根日志级别从 DEBUG 改为 INFO
- 数据库查询日志设置为 INFO
- 数据库结果集日志设置为 WARN
- Hadoop 服务保持 INFO 级别

### 4. 错误信息不足
**问题**: MapReduce 失败时缺少详细的错误信息。

**修复**: 
- 添加了 Job ID 和 Tracking URL 日志
- 添加了完整的堆栈跟踪
- 添加了作业执行持续时间
- 添加了输入文件存在性验证

## 如何验证 MapReduce 是否提交到 YARN

### 1. 检查应用程序日志

启动应用后，查找以下日志消息：

#### 启动时的 Hadoop 配置日志
```
========================================
Hadoop 配置初始化完成
NameNode: hdfs://localhost:9000
ResourceManager: localhost:8032
MapReduce Framework: yarn
HDFS User: wyb
========================================
```

**关键点**: 确认 `MapReduce Framework: yarn`。如果显示 `local`，作业不会提交到 YARN。

#### MapReduce 作业提交日志
当触发数据大屏或 Hadoop 分析时，查找：
```
========================================
开始提交 MapReduce 任务: PigDataAnalysis_1234567890
输入文件: /pig-system/dashboard/pig_data_1234567890.csv
========================================
创建 MapReduce Job: PigDataAnalysis_1234567890
Job 配置验证:
  - fs.defaultFS: hdfs://localhost:9000
  - yarn.resourcemanager.address: localhost:8032
  - mapreduce.framework.name: yarn
  - mapreduce.app-submission.cross-platform: true
========================================
```

#### 成功的作业完成日志
```
========================================
MapReduce 任务详情:
  - Job Name: PigDataAnalysis_1234567890
  - Job ID: job_1234567890_0001
  - Tracking URL: http://localhost:8088/proxy/application_1234567890_0001/
  - 执行状态: 成功
  - 耗时: 15234 ms
========================================
```

**重要**: 记录 `Job ID` 和 `Tracking URL`。

#### 失败的作业日志
```
========================================
猪数据 MapReduce 任务执行失败
  - 错误信息: Connection refused
  - 异常类型: java.net.ConnectException
  - Job ID: job_1234567890_0001
  - Tracking URL: http://localhost:8088/...
  - 堆栈跟踪:
java.net.ConnectException: Connection refused
    at ...
========================================
```

### 2. 检查 YARN ResourceManager Web UI

打开浏览器访问: `http://localhost:8088` 或 `http://192.168.100.10:8088`

在 **Applications** 页面应该能看到：
- 应用程序名称（例如：PigDataAnalysis_1234567890）
- 应用程序 ID（与日志中的 Job ID 匹配）
- 状态（RUNNING、SUCCEEDED、FAILED）
- 用户（应该是 `wyb`）

如果没有看到任何应用程序，可能的原因：
1. MapReduce 框架配置为 `local` 而不是 `yarn`
2. YARN ResourceManager 未运行
3. 网络连接问题
4. 作业提交失败（检查日志）

### 3. 使用命令行检查 YARN 作业

```bash
# 列出所有应用程序
yarn application -list

# 列出所有应用程序（包括已完成的）
yarn application -list -appStates ALL

# 获取特定应用程序的状态
yarn application -status application_1234567890_0001

# 查看应用程序日志
yarn logs -applicationId application_1234567890_0001
```

### 4. 检查 HDFS 文件

验证输入和输出文件：

```bash
# 列出输入文件
hdfs dfs -ls /pig-system/dashboard/

# 查看输入文件内容（前几行）
hdfs dfs -cat /pig-system/dashboard/pig_data_*.csv | head -20

# 列出输出目录
hdfs dfs -ls /pig-system/output/

# 查看输出结果
hdfs dfs -cat /pig-system/output/PigDataAnalysis_*/part-r-00000
```

## 常见问题排查

### 问题 1: MapReduce 框架是 local 而不是 yarn

**症状**: 日志显示 `mapreduce.framework.name: local`

**解决方案**: 
1. 检查 `application.yml`:
   ```yaml
   hadoop:
     mapreduce:
       framework: yarn  # 确保是 yarn 而不是 local
   ```
2. 重启应用

### 问题 2: 连接到 YARN ResourceManager 失败

**症状**: 
```
Connection refused: localhost:8032
```

**解决方案**:
1. 检查 YARN 是否运行:
   ```bash
   jps | grep ResourceManager
   ```
2. 如果未运行，启动 YARN:
   ```bash
   start-yarn.sh
   ```
3. 检查防火墙设置

### 问题 3: HDFS 权限问题

**症状**:
```
Permission denied: user=XXX, access=WRITE
```

**解决方案**:
1. 检查 `application.yml` 中的用户名:
   ```yaml
   hadoop:
     user: wyb  # 应该与 HDFS 用户匹配
   ```
2. 或者在 HDFS 中设置权限:
   ```bash
   hdfs dfs -chmod -R 777 /pig-system
   ```

### 问题 4: 输入文件不存在

**症状**:
```
输入文件不存在: /pig-system/dashboard/pig_data_123.csv
```

**解决方案**:
- 这通常意味着数据上传到 HDFS 失败
- 检查 HDFS 连接
- 检查 HDFS 写入权限
- 查看之前的日志以了解上传失败的原因

### 问题 5: MapReduce 作业挂起

**症状**: 作业停留在 RUNNING 状态很长时间

**可能原因**:
1. 输入数据太大
2. Mapper/Reducer 代码有死循环
3. YARN 资源不足

**解决方案**:
1. 检查 YARN UI 中的作业进度
2. 查看作业日志:
   ```bash
   yarn logs -applicationId application_XXX
   ```
3. 检查 YARN 资源配置

## 临时启用调试日志

如果需要更详细的日志用于调试特定问题，可以编辑 `log4j.properties`:

```properties
# 为特定服务启用 DEBUG
log4j.logger.com.zhu.service.hadoop.HadoopService=DEBUG
log4j.logger.com.zhu.service.dashboard.DashboardService=DEBUG
log4j.logger.com.zhu.config.HadoopConfig=DEBUG

# 为 Hadoop 客户端启用 DEBUG
log4j.logger.org.apache.hadoop=DEBUG

# 为 YARN 客户端启用 DEBUG
log4j.logger.org.apache.hadoop.yarn=DEBUG
```

**注意**: 调试完成后记得改回 INFO，否则日志会很多。

## 测试 API 端点

手动触发 Hadoop 分析以测试 MapReduce 作业：

```bash
# 触发数据大屏（会执行 MapReduce）
curl http://localhost:8080/api/dashboard/data

# 或者直接访问 Hadoop 分析端点
curl http://localhost:8080/api/dashboard/hadoop-analysis
```

观察日志输出以查看 MapReduce 作业提交和执行。

## 成功标志

当 MapReduce 正确工作时，您应该看到：

✅ 日志显示 `mapreduce.framework.name: yarn`
✅ 日志包含 Job ID（例如：`job_1234567890_0001`）
✅ 日志包含 Tracking URL
✅ YARN UI 显示应用程序
✅ 作业完成（SUCCEEDED 状态）
✅ 输出文件存在于 HDFS
✅ 大屏显示 MapReduce 分析结果

## 后续步骤

如果 MapReduce 作业仍然没有出现在 YARN 中：
1. 仔细检查所有日志消息
2. 验证 Hadoop 集群配置
3. 测试简单的 YARN 示例作业
4. 检查网络连接和防火墙
5. 查看 YARN ResourceManager 日志: `$HADOOP_HOME/logs/yarn-*-resourcemanager-*.log`
