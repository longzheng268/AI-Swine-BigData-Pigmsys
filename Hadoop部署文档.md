# Hadoop 部署文档

## 一、环境要求

### 1.1 硬件要求
- **CPU**: 4核以上
- **内存**: 8GB以上（推荐16GB）
- **硬盘**: 100GB以上可用空间

### 1.2 软件要求
- **操作系统**: CentOS 7 / Ubuntu 20.04
- **Java版本**: JDK 1.8
- **SSH**: 已安装并配置免密登录（集群模式需要）

## 二、单机模式部署（适用于开发测试）

### 2.1 下载Hadoop
```bash
# 创建Hadoop用户
sudo useradd -m hadoop
sudo passwd hadoop

# 切换到hadoop用户
su - hadoop

# 下载Hadoop 2.10.2（与项目配置一致）
cd /opt
sudo wget https://archive.apache.org/dist/hadoop/common/hadoop-2.10.2/hadoop-2.10.2.tar.gz

# 解压
sudo tar -xzf hadoop-2.10.2.tar.gz
sudo mv hadoop-2.10.2 /opt/hadoop
sudo chown -R hadoop:hadoop /opt/hadoop
```

### 2.2 配置环境变量
```bash
# 编辑环境变量（hadoop用户）
vi ~/.bashrc

# 添加以下内容
export HADOOP_HOME=/opt/hadoop
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export JAVA_HOME=/opt/jdk1.8

# 生效配置
source ~/.bashrc

# 验证
hadoop version
```

### 2.3 配置Hadoop

#### 2.3.1 配置core-site.xml
```bash
cd $HADOOP_HOME/etc/hadoop
vi core-site.xml
```

```xml
<configuration>
    <!-- HDFS NameNode地址 -->
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
    
    <!-- Hadoop临时目录 -->
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/opt/hadoop/tmp</value>
    </property>
</configuration>
```

#### 2.3.2 配置hdfs-site.xml
```bash
vi hdfs-site.xml
```

```xml
<configuration>
    <!-- NameNode数据存储目录 -->
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/opt/hadoop/data/namenode</value>
    </property>
    
    <!-- DataNode数据存储目录 -->
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/opt/hadoop/data/datanode</value>
    </property>
    
    <!-- 副本数（单机模式为1） -->
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    
    <!-- SecondaryNameNode地址 -->
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>localhost:50090</value>
    </property>
</configuration>
```

#### 2.3.3 配置mapred-site.xml
```bash
cp mapred-site.xml.template mapred-site.xml
vi mapred-site.xml
```

```xml
<configuration>
    <!-- MapReduce框架 -->
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    
    <!-- JobHistory Server地址 -->
    <property>
        <name>mapreduce.jobhistory.address</name>
        <value>localhost:10020</value>
    </property>
    
    <!-- JobHistory Web UI -->
    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>localhost:19888</value>
    </property>
</configuration>
```

#### 2.3.4 配置yarn-site.xml
```bash
vi yarn-site.xml
```

```xml
<configuration>
    <!-- ResourceManager地址 -->
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>
    
    <!-- NodeManager执行程序 -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
</configuration>
```

#### 2.3.5 配置hadoop-env.sh
```bash
vi hadoop-env.sh
# 找到并修改JAVA_HOME
export JAVA_HOME=/opt/jdk1.8
```

### 2.4 创建必要目录
```bash
# 创建数据目录
mkdir -p /opt/hadoop/data/namenode
mkdir -p /opt/hadoop/data/datanode
mkdir -p /opt/hadoop/tmp

# 设置权限
chmod -R 755 /opt/hadoop
```

### 2.5 格式化NameNode
```bash
# 首次启动前必须格式化（会清空数据）
hdfs namenode -format
```

### 2.6 启动Hadoop服务
```bash
# 启动HDFS
start-dfs.sh

# 启动YARN
start-yarn.sh

# 检查服务状态
jps
# 应该看到：
# NameNode
# DataNode
# SecondaryNameNode
# ResourceManager
# NodeManager
```

### 2.7 验证部署
```bash
# 检查HDFS文件系统
hdfs dfs -ls /

# 创建测试目录
hdfs dfs -mkdir -p /pig-system/input
hdfs dfs -mkdir -p /pig-system/output

# 上传测试文件
echo "Hello Hadoop" > test.txt
hdfs dfs -put test.txt /pig-system/input/

# 查看文件
hdfs dfs -cat /pig-system/input/test.txt

# 访问Web UI
# NameNode: http://localhost:50070
# ResourceManager: http://localhost:8088
```

## 三、伪分布式集群部署（适用于小型生产环境）

### 3.1 SSH免密登录配置
```bash
# hadoop用户生成密钥对
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa

# 将公钥添加到authorized_keys
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

# 测试免密登录
ssh localhost
```

### 3.2 配置hosts文件
```bash
sudo vi /etc/hosts
# 添加：
127.0.0.1 localhost pig-server
192.168.100.10 pig-server
```

### 3.3 配置slaves文件
```bash
cd $HADOOP_HOME/etc/hadoop
vi slaves
# 添加：
localhost
```

### 3.4 启动集群
```bash
# 启动所有服务
start-all.sh

# 或分别启动
start-dfs.sh
start-yarn.sh
```

## 四、完全分布式集群部署（多节点）

### 4.1 规划节点
```
主节点（Master）: 192.168.100.10
  - NameNode
  - ResourceManager
  - SecondaryNameNode

从节点1（Slave1）: 192.168.100.11
  - DataNode
  - NodeManager

从节点2（Slave2）: 192.168.100.12
  - DataNode
  - NodeManager
```

### 4.2 配置SSH免密登录（所有节点）
```bash
# 在每个节点执行
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa

# 在主节点将公钥复制到所有从节点
ssh-copy-id hadoop@192.168.100.10
ssh-copy-id hadoop@192.168.100.11
ssh-copy-id hadoop@192.168.100.12
```

### 4.3 修改配置文件
```bash
# core-site.xml - 修改NameNode地址为主节点IP
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://192.168.100.10:9000</value>
</property>

# yarn-site.xml - 修改ResourceManager为主节点IP
<property>
    <name>yarn.resourcemanager.hostname</name>
    <value>192.168.100.10</value>
</property>

# slaves文件 - 添加从节点
192.168.100.11
192.168.100.12
```

### 4.4 同步配置文件到所有节点
```bash
# 在主节点执行
scp -r $HADOOP_HOME/etc/hadoop hadoop@192.168.100.11:/opt/hadoop/
scp -r $HADOOP_HOME/etc/hadoop hadoop@192.168.100.12:/opt/hadoop/
```

### 4.5 启动集群
```bash
# 在主节点格式化NameNode（仅首次）
hdfs namenode -format

# 启动HDFS
start-dfs.sh

# 启动YARN
start-yarn.sh

# 在主节点启动JobHistory Server
mr-jobhistory-daemon.sh start historyserver
```

## 五、服务管理命令

### 5.1 HDFS服务
```bash
# 启动HDFS
start-dfs.sh

# 停止HDFS
stop-dfs.sh

# 单独启动/停止
hadoop-daemon.sh start namenode
hadoop-daemon.sh start datanode
hadoop-daemon.sh stop namenode
hadoop-daemon.sh stop datanode
```

### 5.2 YARN服务
```bash
# 启动YARN
start-yarn.sh

# 停止YARN
stop-yarn.sh

# 单独启动/停止
yarn-daemon.sh start resourcemanager
yarn-daemon.sh start nodemanager
yarn-daemon.sh stop resourcemanager
yarn-daemon.sh stop nodemanager
```

### 5.3 所有服务
```bash
# 启动所有服务
start-all.sh

# 停止所有服务
stop-all.sh
```

### 5.4 JobHistory Server
```bash
# 启动
mr-jobhistory-daemon.sh start historyserver

# 停止
mr-jobhistory-daemon.sh stop historyserver
```

## 六、常用HDFS命令

```bash
# 查看文件列表
hdfs dfs -ls /
hdfs dfs -ls -R /pig-system

# 创建目录
hdfs dfs -mkdir -p /pig-system/input

# 上传文件
hdfs dfs -put localfile.txt /pig-system/input/

# 下载文件
hdfs dfs -get /pig-system/output/result.txt ./

# 查看文件内容
hdfs dfs -cat /pig-system/input/file.txt

# 删除文件/目录
hdfs dfs -rm /pig-system/oldfile.txt
hdfs dfs -rmr /pig-system/olddir

# 查看文件大小
hdfs dfs -du -h /pig-system

# 检查文件系统健康状态
hdfs fsck /
```

## 七、Web界面访问

| 服务 | 地址 | 说明 |
|------|------|------|
| NameNode | http://localhost:50070 | HDFS状态和文件浏览 |
| ResourceManager | http://localhost:8088 | YARN任务监控 |
| JobHistory | http://localhost:19888 | MapReduce作业历史 |
| SecondaryNameNode | http://localhost:50090 | SecondaryNameNode状态 |

## 八、防火墙配置

```bash
# 开放Hadoop相关端口（在主节点执行）
sudo firewall-cmd --permanent --add-port=9000/tcp    # NameNode RPC
sudo firewall-cmd --permanent --add-port=50070/tcp   # NameNode Web UI
sudo firewall-cmd --permanent --add-port=50090/tcp    # SecondaryNameNode
sudo firewall-cmd --permanent --add-port=50010/tcp    # DataNode数据传输
sudo firewall-cmd --permanent --add-port=50020/tcp    # DataNode IPC
sudo firewall-cmd --permanent --add-port=50075/tcp    # DataNode Web UI
sudo firewall-cmd --permanent --add-port=8032/tcp    # ResourceManager
sudo firewall-cmd --permanent --add-port=8088/tcp    # ResourceManager Web UI
sudo firewall-cmd --permanent --add-port=8042/tcp    # NodeManager Web UI
sudo firewall-cmd --permanent --add-port=19888/tcp   # JobHistory Web UI
sudo firewall-cmd --reload
```

## 九、项目集成配置

### 9.1 修改application.yml
```yaml
hadoop:
  namenode: hdfs://192.168.100.10:9000
  resourcemanager: 192.168.100.10:8032
  namenode-web-ui: http://192.168.100.10:50070
  secondary-namenode-web-ui: http://192.168.100.10:50090
  hdfs:
    base-path: /pig-system
    input-path: /pig-system/input
    output-path: /pig-system/output
  mapreduce:
    framework: yarn
  user: hadoop
```

### 9.2 创建HDFS目录
```bash
hdfs dfs -mkdir -p /pig-system/input
hdfs dfs -mkdir -p /pig-system/output
hdfs dfs -chmod -R 777 /pig-system
```

## 十、验证检查清单

- [ ] Hadoop安装成功（hadoop version）
- [ ] NameNode格式化完成
- [ ] HDFS服务启动正常（jps能看到NameNode和DataNode）
- [ ] YARN服务启动正常（jps能看到ResourceManager和NodeManager）
- [ ] 可以创建和访问HDFS目录
- [ ] Web UI可以正常访问
- [ ] 项目可以连接Hadoop集群

## 十一、常见问题

### 问题1：NameNode无法启动
```bash
# 检查日志
tail -f $HADOOP_HOME/logs/hadoop-hadoop-namenode-*.log

# 常见原因：端口被占用或目录权限问题
# 解决：检查9000端口，确保目录权限正确
```

### 问题2：DataNode无法启动
```bash
# 检查是否和NameNode连接
# 常见原因：namenode地址配置错误或网络不通
# 解决：检查core-site.xml中的fs.defaultFS配置
```

### 问题3：YARN任务提交失败
```bash
# 检查ResourceManager日志
tail -f $HADOOP_HOME/logs/yarn-hadoop-resourcemanager-*.log

# 确保mapreduce.framework.name设置为yarn
```

---

