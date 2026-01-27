package com.zhu.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.net.URI;

/**
 * Hadoop 配置类
 * 用于配置 HDFS 文件系统和 MapReduce 任务
 */
@Slf4j
@Configuration
public class HadoopConfig {

    @Value("${hadoop.namenode}")
    private String nameNode;

    @Value("${hadoop.resourcemanager}")
    private String resourceManager;

    @Value("${hadoop.user}")
    private String hdfsUser;

    @Value("${hadoop.mapreduce.framework}")
    private String mapreduceFramework;

    /**
     * 获取 Hadoop 配置对象
     */
    @Bean
    public org.apache.hadoop.conf.Configuration hadoopConfiguration() {
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        
        // Windows 环境配置：禁用本地 Hadoop home 检查，避免 winutils.exe 错误
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows 环境下，设置 hadoop.home.dir 为临时目录，避免检查本地 Hadoop 安装
            String tempDir = System.getProperty("java.io.tmpdir");
            configuration.set("hadoop.home.dir", tempDir);
            // 禁用本地文件系统检查，因为我们连接的是远程集群
            System.setProperty("hadoop.home.dir", tempDir);
            log.info("Windows 环境检测到，已设置 hadoop.home.dir={}", tempDir);
        }
        
        // 设置 HDFS 地址
        configuration.set("fs.defaultFS", nameNode);
        
        // 设置 YARN ResourceManager 地址
        configuration.set("yarn.resourcemanager.address", resourceManager);
        
        // 设置 MapReduce 框架为 YARN
        configuration.set("mapreduce.framework.name", mapreduceFramework);
        
        // 跨平台提交作业配置（Windows 提交到 Linux 集群）
        configuration.set("mapreduce.app-submission.cross-platform", "true");
        
        // 设置用户身份
        System.setProperty("HADOOP_USER_NAME", hdfsUser);
        
        // JobHistoryServer 地址（可选）
        // configuration.set("mapreduce.jobhistory.address", "localhost:10020");
        
        log.info("Hadoop 配置初始化完成：NameNode={}, User={}", nameNode, hdfsUser);
        
        return configuration;
    }

    /**
     * 获取 HDFS 文件系统对象
     * 使用 @Lazy 延迟加载，避免启动时立即连接
     */
    @Bean
    @Lazy
    public FileSystem fileSystem() throws IOException {
        try {
            org.apache.hadoop.conf.Configuration configuration = hadoopConfiguration();
            FileSystem fileSystem = FileSystem.get(URI.create(nameNode), configuration);
            log.info("HDFS 文件系统连接成功");
            return fileSystem;
        } catch (Exception e) {
            log.error("HDFS 文件系统连接失败: {}", e.getMessage());
            // 如果连接失败，返回 null 或抛出异常，根据业务需求决定
            throw new IOException("无法连接到 HDFS: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 MapReduce Job 实例
     */
    public Job createJob(String jobName) throws IOException {
        org.apache.hadoop.conf.Configuration configuration = hadoopConfiguration();
        Job job = Job.getInstance(configuration, jobName);
        log.info("创建 MapReduce Job: {}", jobName);
        return job;
    }
}

