package com.zhu.service.hadoop;

import com.zhu.config.HadoopConfig;
import com.zhu.hadoop.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hadoop 服务层
 * 提供 HDFS 文件操作和 MapReduce 任务管理
 */
@Slf4j
@Service
public class HadoopService {

    @Autowired
    private FileSystem fileSystem;

    @Autowired
    private HadoopConfig hadoopConfig;

    @Value("${hadoop.hdfs.input-path}")
    private String inputPath;

    @Value("${hadoop.hdfs.output-path}")
    private String outputPath;

    /**
     * 上传文件到 HDFS
     */
    public boolean uploadToHDFS(String localPath, String hdfsPath) {
        try {
            Path srcPath = new Path(localPath);
            Path dstPath = new Path(hdfsPath);
            
            // 如果目标文件存在，先删除
            if (fileSystem.exists(dstPath)) {
                fileSystem.delete(dstPath, true);
            }
            
            fileSystem.copyFromLocalFile(srcPath, dstPath);
            log.info("文件上传成功：{} -> {}", localPath, hdfsPath);
            return true;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return false;
        }
    }

    /**
     * 从 HDFS 下载文件
     */
    public boolean downloadFromHDFS(String hdfsPath, String localPath) {
        try {
            Path srcPath = new Path(hdfsPath);
            Path dstPath = new Path(localPath);
            
            fileSystem.copyToLocalFile(false, srcPath, dstPath, true);
            log.info("文件下载成功：{} -> {}", hdfsPath, localPath);
            return true;
        } catch (IOException e) {
            log.error("文件下载失败", e);
            return false;
        }
    }

    /**
     * 删除 HDFS 文件或目录
     */
    public boolean deleteFromHDFS(String hdfsPath) {
        try {
            Path path = new Path(hdfsPath);
            if (fileSystem.exists(path)) {
                boolean result = fileSystem.delete(path, true);
                log.info("删除 HDFS 路径：{}, 结果：{}", hdfsPath, result);
                return result;
            }
            return false;
        } catch (IOException e) {
            log.error("删除文件失败", e);
            return false;
        }
    }

    /**
     * 列出 HDFS 目录下的文件
     */
    public List<String> listFiles(String hdfsPath) {
        List<String> fileList = new ArrayList<>();
        try {
            Path path = new Path(hdfsPath);
            if (fileSystem.exists(path)) {
                FileStatus[] statuses = fileSystem.listStatus(path);
                for (FileStatus status : statuses) {
                    fileList.add(status.getPath().toString());
                }
            }
            log.info("列出目录 {} 的文件，共 {} 个", hdfsPath, fileList.size());
        } catch (IOException e) {
            log.error("列出文件失败", e);
        }
        return fileList;
    }

    /**
     * 读取 HDFS 文件内容
     */
    public String readFileContent(String hdfsPath) {
        StringBuilder content = new StringBuilder();
        try {
            Path path = new Path(hdfsPath);
            if (fileSystem.exists(path)) {
                FSDataInputStream inputStream = fileSystem.open(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
                log.info("读取文件成功：{}", hdfsPath);
            }
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
        return content.toString();
    }

    /**
     * 写入内容到 HDFS 文件
     */
    public boolean writeToHDFS(String hdfsPath, String content) {
        try {
            Path path = new Path(hdfsPath);
            
            // 如果文件存在，先删除
            if (fileSystem.exists(path)) {
                fileSystem.delete(path, true);
            }
            
            FSDataOutputStream outputStream = fileSystem.create(path);
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.close();
            log.info("写入文件成功：{}", hdfsPath);
            return true;
        } catch (IOException e) {
            log.error("写入文件失败", e);
            return false;
        }
    }

    /**
     * 执行猪数据分析 MapReduce 任务
     */
    public Map<String, Object> runPigDataAnalysis(String inputFile) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String jobName = "PigDataAnalysis_" + System.currentTimeMillis();
            log.info("========================================");
            log.info("开始提交 MapReduce 任务: {}", jobName);
            log.info("输入文件: {}", inputFile);
            
            Job job = hadoopConfig.createJob(jobName);
            
            // 设置 Jar 类
            job.setJarByClass(PigDataAnalysisMapper.class);
            
            // 设置 Mapper 和 Reducer 类
            job.setMapperClass(PigDataAnalysisMapper.class);
            job.setReducerClass(PigDataAnalysisReducer.class);
            
            // 设置输出类型
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            // 设置输入输出路径
            // 注意：inputFile 已经是完整的 HDFS 路径
            String input = inputFile;
            String output = outputPath + "/" + jobName;
            
            log.info("MapReduce 输入路径: {}", input);
            log.info("MapReduce 输出路径: {}", output);
            
            // 验证输入文件是否存在
            Path inputPath = new Path(input);
            if (!fileSystem.exists(inputPath)) {
                String errorMsg = "输入文件不存在: " + input;
                log.error(errorMsg);
                result.put("success", false);
                result.put("error", errorMsg);
                return result;
            }
            log.info("输入文件验证成功，文件存在: {}", input);
            
            FileInputFormat.addInputPath(job, new Path(input));
            FileOutputFormat.setOutputPath(job, new Path(output));
            
            // 删除已存在的输出目录
            Path outputPath = new Path(output);
            if (fileSystem.exists(outputPath)) {
                log.info("删除已存在的输出目录: {}", output);
                fileSystem.delete(outputPath, true);
            }
            
            // 提交任务并等待完成
            log.info("提交 MapReduce 任务到 YARN 集群...");
            long startTime = System.currentTimeMillis();
            
            boolean success = job.waitForCompletion(true);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("MapReduce 任务执行完成，耗时: {} ms", duration);
            
            // 获取 Job ID（YARN Application ID）
            String jobId = job.getJobID() != null ? job.getJobID().toString() : "未知";
            String trackingUrl = job.getTrackingURL();
            
            log.info("========================================");
            log.info("MapReduce 任务详情:");
            log.info("  - Job Name: {}", jobName);
            log.info("  - Job ID: {}", jobId);
            log.info("  - Tracking URL: {}", trackingUrl);
            log.info("  - 执行状态: {}", success ? "成功" : "失败");
            log.info("  - 耗时: {} ms", duration);
            log.info("========================================");
            
            result.put("success", success);
            result.put("jobName", jobName);
            result.put("jobId", jobId);
            result.put("trackingUrl", trackingUrl);
            result.put("outputPath", output);
            result.put("duration", duration);
            
            if (success) {
                // 读取结果
                String resultPath = output + "/part-r-00000";
                log.info("读取 MapReduce 结果: {}", resultPath);
                String resultContent = readFileContent(resultPath);
                result.put("result", parseAnalysisResult(resultContent));
                log.info("猪数据分析任务完成：{}", jobName);
            } else {
                String errorMsg = "MapReduce 任务执行失败，请检查 YARN ResourceManager 日志";
                log.error(errorMsg);
                result.put("error", errorMsg);
            }
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("执行 MapReduce 任务异常");
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            log.error("========================================", e);
            
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("exceptionType", e.getClass().getName());
            result.put("stackTrace", getStackTraceString(e));
        }
        
        return result;
    }

    /**
     * 执行环境数据分析 MapReduce 任务
     */
    public Map<String, Object> runEnvironmentDataAnalysis(String inputFile) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String jobName = "EnvironmentDataAnalysis_" + System.currentTimeMillis();
            log.info("========================================");
            log.info("开始提交环境数据分析 MapReduce 任务: {}", jobName);
            log.info("输入文件: {}", inputFile);
            
            Job job = hadoopConfig.createJob(jobName);
            
            // 设置 Jar 类
            job.setJarByClass(EnvironmentDataAnalysisMapper.class);
            
            // 设置 Mapper 和 Reducer 类
            job.setMapperClass(EnvironmentDataAnalysisMapper.class);
            job.setReducerClass(EnvironmentDataAnalysisReducer.class);
            
            // 设置 Mapper 输出类型
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(DoubleWritable.class);
            
            // 设置最终输出类型
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            
            // 设置输入输出路径
            // 注意：inputFile 已经是完整的 HDFS 路径
            String input = inputFile;
            String output = outputPath + "/" + jobName;
            
            log.info("MapReduce 输入路径: {}", input);
            log.info("MapReduce 输出路径: {}", output);
            
            // 验证输入文件是否存在
            Path inputPath = new Path(input);
            if (!fileSystem.exists(inputPath)) {
                String errorMsg = "输入文件不存在: " + input;
                log.error(errorMsg);
                result.put("success", false);
                result.put("error", errorMsg);
                return result;
            }
            log.info("输入文件验证成功，文件存在: {}", input);
            
            FileInputFormat.addInputPath(job, new Path(input));
            FileOutputFormat.setOutputPath(job, new Path(output));
            
            // 删除已存在的输出目录
            Path outputPath = new Path(output);
            if (fileSystem.exists(outputPath)) {
                log.info("删除已存在的输出目录: {}", output);
                fileSystem.delete(outputPath, true);
            }
            
            // 提交任务并等待完成
            log.info("提交环境数据分析 MapReduce 任务到 YARN 集群...");
            long startTime = System.currentTimeMillis();
            
            boolean success = job.waitForCompletion(true);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("环境数据分析 MapReduce 任务执行完成，耗时: {} ms", duration);
            
            // 获取 Job ID（YARN Application ID）
            String jobId = job.getJobID() != null ? job.getJobID().toString() : "未知";
            String trackingUrl = job.getTrackingURL();
            
            log.info("========================================");
            log.info("MapReduce 任务详情:");
            log.info("  - Job Name: {}", jobName);
            log.info("  - Job ID: {}", jobId);
            log.info("  - Tracking URL: {}", trackingUrl);
            log.info("  - 执行状态: {}", success ? "成功" : "失败");
            log.info("  - 耗时: {} ms", duration);
            log.info("========================================");
            
            result.put("success", success);
            result.put("jobName", jobName);
            result.put("jobId", jobId);
            result.put("trackingUrl", trackingUrl);
            result.put("outputPath", output);
            result.put("duration", duration);
            
            if (success) {
                // 读取结果
                String resultPath = output + "/part-r-00000";
                log.info("读取环境数据分析 MapReduce 结果: {}", resultPath);
                String resultContent = readFileContent(resultPath);
                result.put("result", parseEnvironmentAnalysisResult(resultContent));
                log.info("环境数据分析任务完成：{}", jobName);
            } else {
                String errorMsg = "环境数据分析 MapReduce 任务执行失败，请检查 YARN ResourceManager 日志";
                log.error(errorMsg);
                result.put("error", errorMsg);
            }
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("执行环境数据分析 MapReduce 任务异常");
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            log.error("========================================", e);
            
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("exceptionType", e.getClass().getName());
            result.put("stackTrace", getStackTraceString(e));
        }
        
        return result;
    }

    /**
     * 解析猪数据分析结果
     */
    private Map<String, Integer> parseAnalysisResult(String content) {
        Map<String, Integer> dataMap = new HashMap<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\t");
            if (parts.length >= 2) {
                dataMap.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
        return dataMap;
    }

    /**
     * 解析环境数据分析结果
     */
    private Map<String, Map<String, Object>> parseEnvironmentAnalysisResult(String content) {
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\t");
            if (parts.length >= 2) {
                String metric = parts[0];
                String[] values = parts[1].split(",");
                if (values.length >= 4) {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("average", Double.parseDouble(values[0]));
                    stats.put("max", Double.parseDouble(values[1]));
                    stats.put("min", Double.parseDouble(values[2]));
                    stats.put("count", Integer.parseInt(values[3]));
                    dataMap.put(metric, stats);
                }
            }
        }
        return dataMap;
    }

    /**
     * 检查 HDFS 连接状态
     */
    public boolean checkConnection() {
        try {
            return fileSystem.exists(new Path("/"));
        } catch (IOException e) {
            log.error("HDFS 连接检查失败", e);
            return false;
        }
    }
    
    /**
     * 获取异常堆栈跟踪字符串
     * 使用 Apache Commons Lang3 的 ExceptionUtils 提供更健壮的实现
     */
    private String getStackTraceString(Exception e) {
        return ExceptionUtils.getStackTrace(e);
    }
}

