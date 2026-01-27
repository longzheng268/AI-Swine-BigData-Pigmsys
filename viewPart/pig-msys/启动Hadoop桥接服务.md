# 🚀 Hadoop 桥接服务部署指南

## 📦 第一步：安装依赖

在 `viewPart/pig-msys` 目录下安装 Node.js 依赖：

```bash
cd /Users/wyb/hadoop_workspace/AI-Swine-BigData-Pigmsys/viewPart/pig-msys
npm install express cors
```

## 🔧 第二步：配置 HADOOP_HOME

确保 HADOOP_HOME 环境变量已设置：

```bash
# 查看当前 HADOOP_HOME
echo $HADOOP_HOME

# 如果未设置，临时设置（根据你的实际路径）
export HADOOP_HOME=/opt/homebrew/opt/hadoop/libexec

# 或者永久设置（添加到 ~/.zshrc）
echo 'export HADOOP_HOME=/opt/homebrew/opt/hadoop/libexec' >> ~/.zshrc
source ~/.zshrc
```

## 🚀 第三步：启动桥接服务

在 `viewPart/pig-msys/src/api` 目录下启动服务：

```bash
cd /Users/wyb/hadoop_workspace/AI-Swine-BigData-Pigmsys/viewPart/pig-msys/src/api
node hadoop-server.js
```

成功启动后会看到：
```
🚀 ========================================
🚀 Hadoop Bridge API 服务已启动
🚀 监听端口: 3100
🚀 健康检查: http://localhost:3100/health
🚀 HADOOP_HOME: /opt/homebrew/opt/hadoop/libexec
🚀 ========================================
```

## ✅ 第四步：测试服务

### 方式 1: 健康检查
```bash
curl http://localhost:3100/health
```

### 方式 2: 手动提交任务
```bash
curl -X POST http://localhost:3100/api/hadoop/submit-environment-analysis
```

## 🎨 第五步：启动前端

保持桥接服务运行，新开一个终端启动前端：

```bash
cd /Users/wyb/hadoop_workspace/AI-Swine-BigData-Pigmsys/viewPart/pig-msys
npm run serve
```

## 🧪 第六步：测试完整流程

1. 访问大屏：`http://localhost:8080/#/dashboard/bigscreen`
2. **打开大屏时**：会自动 fire-and-forget 提交一个 Hadoop 任务
3. **点击"刷新数据"**：会再次提交一个新任务
4. 查看浏览器控制台：应该能看到 `✅ Hadoop 任务已提交` 的日志
5. 查看桥接服务终端：应该能看到任务提交的详细日志

## 🛠️ 故障排查

### 问题 1: `Cannot find module 'express'`
```bash
cd /Users/wyb/hadoop_workspace/AI-Swine-BigData-Pigmsys/viewPart/pig-msys
npm install express cors
```

### 问题 2: 前端报错 `Network Error` 或 `ERR_CONNECTION_REFUSED`
- 确保桥接服务正在运行（端口 3100）
- 检查防火墙是否阻止了 3100 端口

### 问题 3: `找不到 hadoop-streaming jar 文件`
```bash
# 检查 jar 文件是否存在
ls $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar

# 如果不存在，确认 Hadoop 安装完整
hadoop version
```

### 问题 4: `hadoop: command not found`
```bash
# 确保 Hadoop bin 目录在 PATH 中
export PATH=$HADOOP_HOME/bin:$PATH

# 测试 hadoop 命令
hadoop version
```

## 🔄 后台运行（可选）

### 使用 nohup
```bash
cd /Users/wyb/hadoop_workspace/AI-Swine-BigData-Pigmsys/viewPart/pig-msys/src/api
nohup node hadoop-server.js > hadoop-bridge.log 2>&1 &

# 查看日志
tail -f hadoop-bridge.log

# 停止服务
ps aux | grep hadoop-server
kill <PID>
```

### 使用 PM2（推荐）
```bash
# 安装 PM2
npm install -g pm2

# 启动服务
cd /Users/wyb/hadoop_workspace/AI-Swine-BigData-Pigmsys/viewPart/pig-msys/src/api
pm2 start hadoop-server.js --name hadoop-bridge

# 查看状态
pm2 status

# 查看日志
pm2 logs hadoop-bridge

# 停止服务
pm2 stop hadoop-bridge

# 删除服务
pm2 delete hadoop-bridge
```

## 📝 工作流程说明

1. **用户打开大屏** → 前端 `BigScreen.vue` mounted 钩子触发
2. **调用 `submitHadoopJobFire()`** → 发送 POST 请求到 `http://localhost:3100/api/hadoop/submit-environment-analysis`
3. **Node.js 桥接服务** → 接收请求，使用 `child_process.exec` 执行 hadoop streaming 命令
4. **立即返回响应** → 返回 `{ code: 200, message: '任务已提交', data: { pid: ... } }`
5. **Hadoop 任务在后台执行** → 不阻塞前端，用户可以继续浏览大屏
6. **点击刷新数据** → 重复步骤 2-5，提交新任务

## ⚠️ 注意事项

1. **端口占用**：确保 3100 端口未被其他服务占用
2. **HDFS 路径**：确保 `/user/hadoop/pig_data/environment` 等路径存在且有权限
3. **Hadoop 集群**：确保 Hadoop 集群正常运行（NameNode、DataNode 等）
4. **网络连接**：前端和桥接服务必须能互相访问
5. **跨域问题**：桥接服务已配置 CORS，允许所有来源（生产环境需限制）

## 🎯 生产环境建议

1. 使用环境变量配置端口和路径
2. 添加请求认证（JWT、API Key）
3. 限制 CORS 为特定域名
4. 使用 systemd 或 Docker 部署
5. 添加日志持久化和监控
6. 设置请求频率限制

## 📚 相关文件

- 桥接核心逻辑：`viewPart/pig-msys/src/api/hadoop-bridge.js`
- API 服务器：`viewPart/pig-msys/src/api/hadoop-server.js`
- 前端 API：`viewPart/pig-msys/src/api/dashboard.js`
- 大屏组件：`viewPart/pig-msys/src/views/dashboard/BigScreen.vue`
