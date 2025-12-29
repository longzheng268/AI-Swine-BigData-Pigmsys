# 启动 Hadoop 集群

## 切换到 Hadoop 用户

```bash
su hadoop
```

## 启动 Hadoop 集群

```bash
# 启动所有 Hadoop 进程（NameNode、DataNode、ResourceManager 等）
start-all.sh
```

**注意**：如果出现密码提示（`hadoop@0.0.0.0's password:`），输入密码：`1`

## 验证启动状态

```bash
jps
```

应该看到以下进程：
```
2308 NodeManager
1545 NameNode
1850 SecondaryNameNode
2011 ResourceManager
2876 Jps
1677 DataNode
```

出现这些进程说明启动成功了。

## Web UI 访问

浏览器打开以下网址验证集群搭建成功：

- **SecondaryNameNode**: http://192.168.100.10:50090/
- **NameNode**: http://192.168.100.10:50070/
- **ResourceManager**: http://192.168.100.10:8088/

## 停止 Hadoop 集群

```bash
# 停止所有 Hadoop 进程（项目确认没问题再进行此操作）
stop-all.sh
```

## HDFS 常用命令

```bash
# 查看 HDFS 中的文件内容
hadoop fs -cat /pig-system/input/environment_data_20251027093000.csv

# 列出 HDFS 目录内容
hadoop fs -ls /pig-system/input/

# 上传文件到 HDFS
hadoop fs -put localfile.txt /pig-system/input/

# 从 HDFS 下载文件
hadoop fs -get /pig-system/output/result.txt ./
```



