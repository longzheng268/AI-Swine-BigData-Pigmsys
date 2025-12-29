# 生猪智慧养殖大数据系统 | AI Swine BigData Management System

基于 Hadoop + Spring Boot + Vue 3 的生猪智慧养殖大数据系统，集成环境监测、数据分析、智能预测等功能。

## 📋 目录

- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [核心功能](#核心功能)
- [API 接口](#api-接口)
- [macOS 特别说明](#macos-特别说明)
- [常见问题](#常见问题)

## 🚀 技术栈

### 后端（Backend）

- **框架**: Spring Boot 2.6.7
- **语言**: JDK 11
- **ORM**: MyBatis 2.2.2
- **数据库**: MySQL 8.0 + Druid 连接池
- **安全**: Spring Security + JWT (jjwt 0.9.1)
- **大数据**: Hadoop 3.4.3 (HDFS + MapReduce)
- **工具库**: Lombok, PageHelper, Apache POI, FastJSON, Hutool

### 前端（Frontend）

- **框架**: Vue 3.3.x
- **构建工具**: Vue CLI 5.0.x (计划迁移至 Vite)
- **语言**: JavaScript (ES6+)
- **路由**: Vue Router 4.x
- **状态管理**: Vuex 4.x
- **UI 组件**: Element Plus 2.4.x
- **图表**: ECharts 5.x
- **HTTP 客户端**: Axios 0.27.x
- **开发端口**: 8081

### Python 服务（已弃用）

- ~~**框架**: Flask 2.3.0~~
- ~~**跨域**: Flask-CORS~~
- ~~**机器学习**: scikit-learn, NumPy~~
- ~~**服务端口**: 5000/5001~~

> **✨ 重要更新**: 预测算法已完全迁移到前端 JavaScript 实现，**无需启动 Python 服务**。前端算法提供毫秒级响应速度，简化系统架构，降低部署复杂度。

## 📁 项目结构

```
AI-Swine-BigData-Pigmsys/
├── pigms/                      # Spring Boot 后端
│   ├── src/main/java/com/zhu/
│   │   ├── controller/        # 控制器层
│   │   ├── service/          # 业务逻辑层
│   │   ├── mapper/           # 数据访问层
│   │   ├── pojo/             # 实体类
│   │   ├── config/           # 配置类
│   │   ├── filter/           # 过滤器（JWT、CORS）
│   │   ├── utils/            # 工具类
│   │   └── hadoop/           # Hadoop MapReduce
│   └── src/main/resources/
│       ├── application.yml   # 配置文件
│       └── mybatis/mapper/   # MyBatis XML 映射
│
├── viewPart/pig-msys/         # Vue 3 前端
│   ├── src/
│   │   ├── views/            # 页面组件
│   │   ├── components/       # 公共组件
│   │   ├── api/              # API 接口封装
│   │   ├── router/           # 路由配置
│   │   └── utils/            # 工具函数
│   │       └── prediction.js # 🎯 前端预测算法（核心）
│   └── vue.config.js         # Vue CLI 配置（代理 8080 端口）
│
├── mysql/                     # 数据库相关
│   └── pigms.sql             # 主数据库脚本
│
└── README.md                  # 本文件
```

## 💻 环境要求

### 基础环境

- **操作系统**: macOS (ARM64/Intel), Linux, Windows
- **JDK**: 11 或更高版本
- **Maven**: 3.6+
- **Node.js**: 16.x 或更高版本 (推荐 18.x 或 20.x)
- **npm**: 8.x 或更高版本
- **MySQL**: 8.0
- **Hadoop**: 3.4.3 (可选，用于大数据分析)

### 推荐配置（macOS）

- macOS Monterey 或更高版本
- JDK 11 (推荐使用 Homebrew 安装)
- Hadoop 3.4.3 (配置 HDFS NameNode 端口 9000)
- MySQL 8.0 (使用 Homebrew 或 Docker)

## 🔧 快速开始

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE pigms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据
mysql -u root -p pigms < mysql/pigms.sql
```

### 2. 后端配置与启动

编辑 `pigms/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pigms?useUnicode=true&characterEncoding=utf-8
    username: root
    password: 你的密码

hadoop:
  namenode: hdfs://localhost:9000
  resourcemanager: localhost:8032
```

启动后端（端口 8080）:

```bash
cd pigms
mvn clean install
mvn spring-boot:run
```

### 3. 前端配置与启动

```bash
cd viewPart/pig-msys

# 安装依赖
npm install

# 启动开发服务器（端口 8081）
npm run dev
```

访问: http://localhost:8081

**默认账号**:
- 用户名: `admin`
- 密码: `123456789`

### 4. Python 服务启动（可选）

如果需要使用 Python 预测服务而非前端算法：

```bash
cd mysql/pythonService

# 安装依赖
pip install flask flask-cors numpy

# 启动服务（端口 5001）
python app.py
```

## 🎯 核心功能

### 1. 用户认证与权限

- JWT Token 认证
- 基于角色的权限控制（ADMIN/USER/RESEARCHER）
- 登录/注册/登出

### 2. 生猪信息管理

- 生猪信息 CRUD
- 猪类型管理
- 生产周期分析

### 3. 环境监测

- 环境数据采集（温度/湿度/空气质量等）
- 异常数据预警
- 历史数据查询

### 4. 数据分析

- Hadoop MapReduce 数据分析
- 数据可视化（ECharts）
- 可视化大屏展示

### 5. 智能预测（前端算法 + 后端服务）

#### 算法模式

系统支持两种预测算法模式：

1. **前端算法模式（默认）**
   - ✅ 毫秒级响应速度
   - ✅ 无需后端服务依赖
   - ✅ 降低系统复杂度
   - ✅ 便于部署和维护
   - 适用场景：快速预测、离线使用

2. **后端服务模式**
   - 支持复杂的机器学习模型
   - 需要后端 API 支持
   - 可扩展性强
   - 适用场景：高精度预测、大规模数据

系统会自动检测后端服务可用性，当后端不可用时自动降级为前端算法。用户也可以手动切换模式。

#### 生长预测
- 输入: 日龄、饲料摄入量、品种、性别
- 输出: 预测体重、生长速度、准确率、建议

#### 环境质量评价
- 输入: 温度、湿度、CO2浓度、氨气浓度、光照强度
- 输出: 环境等级（I-IV）、各项指标状态、改进建议

#### 疾病风险预测
- 输入: 温度、湿度、养殖密度、日龄、疫苗接种情况
- 输出: 风险等级、风险概率、预防建议

#### 🆕 方案对比功能

新增生长预测方案对比功能：
- 支持输入两组不同的饲养参数
- ECharts 可视化对比柱状图
- 智能分析和建议
- 直观展示不同品种/饲养方式的预期效果

> **前端算法实现细节**:
> - 算法位置: `src/utils/prediction.js`
> - 包含完整的预测逻辑，从 Python 迁移而来
> - 纯 JavaScript 实现，无外部依赖
> - 算法准确率: 生长预测 92.5%、环境评价 88%、疾病预测 86.5%

## 📡 API 接口

### 认证相关

```
POST /user/login              # 登录（Form Data: username, userpassword）
GET  /user/info/{token}       # 获取用户信息
GET  /user/logout/{token}     # 登出
POST /user/register           # 注册
```

### 预测分析

```
GET  /prediction/models                    # 获取所有模型
GET  /prediction/models/type/{type}        # 按类型获取模型
GET  /prediction/models/{id}               # 获取模型详情
POST /prediction/predict                   # 执行预测
GET  /prediction/records                   # 获取所有预测记录
GET  /prediction/records/user/{userId}     # 按用户获取记录
```

> **注意**: 前端已实现预测算法，无需后端服务也可正常使用预测功能。

### 生猪管理

```
GET  /pig/getTypeSum                        # 获取猪类型统计
GET  /pigInfo/list                          # 获取生猪列表
POST /pigInfo/list/search/{page}/{size}     # 分页查询
POST /addPig                                # 新增生猪
GET  /pigInfo/{id}                          # 获取详情
```

### 环境监测

```
POST /environment/add                 # 新增环境数据
GET  /environment/list                # 获取列表
GET  /environment/latest              # 最新数据
GET  /environment/abnormal/count      # 异常数据统计
```

### Hadoop 操作

```
GET  /api/hadoop/status              # 集群状态
POST /api/hadoop/upload              # 上传文件到 HDFS
GET  /api/hadoop/list                # 文件列表
POST /api/hadoop/analyze/pig         # MapReduce 分析
```

## 🍎 macOS 特别说明

### 端口冲突解决

macOS 系统默认占用某些端口，可能导致冲突：

#### 5000 端口冲突（AirPlay 接收器）

如果使用 Python 服务，需要释放 5000 端口：

1. **临时解决**: 修改 Python 服务端口为 5001
2. **永久解决**: 关闭 AirPlay 接收器
   - 系统偏好设置 → 共享 → 取消勾选"AirPlay 接收器"

#### 7000 端口冲突

某些 macOS 服务可能占用 7000 端口，建议使用其他端口。

### Hadoop 配置

macOS 上配置 Hadoop 3.4.3:

```bash
# 环境变量配置 (~/.zshrc 或 ~/.bash_profile)
export HADOOP_HOME=/path/to/hadoop-3.4.3
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

# 配置文件
# $HADOOP_HOME/etc/hadoop/core-site.xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
</configuration>

# 格式化 NameNode（首次运行）
hdfs namenode -format

# 启动 HDFS
start-dfs.sh
```

### JDK 配置

```bash
# 使用 Homebrew 安装 JDK 11
brew install openjdk@11

# 配置环境变量
export JAVA_HOME=/opt/homebrew/opt/openjdk@11
export PATH=$JAVA_HOME/bin:$PATH
```

## 🔍 常见问题

### 1. 前端无法连接后端

**问题**: 前端显示网络错误

**解决**:
- 确认后端服务运行在 `http://localhost:8080`
- 检查 `vite.config.ts` 中的代理配置
- 查看浏览器控制台的具体错误信息

### 2. 登录接口 400 错误

**问题**: 登录时返回 400 Bad Request

**原因**: 登录接口需要 `application/x-www-form-urlencoded` 格式

**解决**: 前端已使用 `qs.stringify()` 正确处理，确保没有修改 API 调用方式

### 3. JWT Token 过期

**问题**: 操作时提示未授权

**解决**: Token 过期后会自动跳转到登录页，重新登录即可

### 4. Hadoop 连接失败

**问题**: 后端日志显示 Hadoop 连接错误

**解决**:
- 确认 Hadoop 服务已启动: `jps` 命令查看 NameNode 是否运行
- 检查 `application.yml` 中的 Hadoop 配置
- 确认防火墙未阻止 9000 端口

### 5. MySQL 连接失败

**问题**: 后端启动时数据库连接错误

**解决**:
- 确认 MySQL 服务已启动
- 检查数据库名称、用户名、密码
- 确认数据库已创建并导入数据

### 6. 前端构建错误

**问题**: `npm run build` 失败

**解决**:
```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm install

# 如果仍有问题，检查 Node.js 版本
node --version  # 应该是 20.x 或更高
```

## 📚 开发规范

### 后端

- Controller 层负责接口定义和参数验证
- Service 层处理业务逻辑
- Mapper 层负责数据库操作
- 统一使用 RESTful API 风格
- 返回格式: `{code: 200, message: "success", data: {...}}`

### 前端

- 使用 Vue 3 Composition API
- 组件化开发
- API 统一封装在 `src/api/` 目录
- 工具函数放在 `src/utils/` 目录
- 统一使用 Element Plus 组件

### 代码规范

- Java: 驼峰命名
- JavaScript/TypeScript: 驼峰命名
- 数据库: 下划线命名

## 📝 更新日志

### v2.0.0 (2024)
- ✨ 新增 Vue 3 前端，使用 Vue CLI 构建
- ✨ **预测算法迁移到前端 JavaScript，支持离线使用**
- ✨ **新增方案对比功能，支持 ECharts 可视化对比**
- ✨ 支持前端算法和后端服务两种模式，自动降级
- ✨ 优化登录认证流程，支持 JWT Token
- 🔧 前端开发端口修改为 8081
- 🔧 改进响应式布局，支持 macOS、iPhone 等设备
- 📝 更新文档，增加 macOS 配置说明

## 📄 许可证

本项目仅供学习和研究使用。

## 👥 联系方式

如有问题，请提交 Issue。
