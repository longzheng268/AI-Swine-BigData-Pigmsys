package com.zhu.controller;

import com.zhu.annotation.Log;
import com.zhu.annotation.RequiresRole;
import com.zhu.service.hadoop.HadoopService;
import com.zhu.vo.HadoopJobVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hadoop 分布式计算控制器
 * 提供 HDFS 文件管理和 MapReduce 任务执行接口
 */
@Slf4j
@RestController
@RequestMapping("/api/hadoop")
@CrossOrigin
public class HadoopController {

    @Autowired
    private HadoopService hadoopService;

    /**
     * 检查 Hadoop 连接状态
     */
    @GetMapping("/status")
    @Log("检查Hadoop连接状态")
    public ResponseEntity<Map<String, Object>> checkStatus() {
        Map<String, Object> response = new HashMap<>();
        boolean connected = hadoopService.checkConnection();
        response.put("connected", connected);
        response.put("message", connected ? "Hadoop 集群连接正常" : "Hadoop 集群连接失败");
        return ResponseEntity.ok(response);
    }

    /**
     * 上传文件到 HDFS
     */
    @PostMapping("/upload")
    @Log("上传文件到HDFS")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "hdfsPath", required = false) String hdfsPath) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 保存临时文件
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = file.getOriginalFilename();
            File tempFile = new File(tempDir, fileName);
            file.transferTo(tempFile);
            
            // 设置 HDFS 路径
            if (hdfsPath == null || hdfsPath.isEmpty()) {
                hdfsPath = "/pig-system/input/" + fileName;
            }
            
            // 上传到 HDFS
            boolean success = hadoopService.uploadToHDFS(tempFile.getAbsolutePath(), hdfsPath);
            
            // 删除临时文件
            tempFile.delete();
            
            response.put("success", success);
            response.put("message", success ? "文件上传成功" : "文件上传失败");
            response.put("hdfsPath", hdfsPath);
            response.put("fileName", fileName);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            response.put("success", false);
            response.put("message", "文件上传失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 从 HDFS 下载文件
     */
    @GetMapping("/download")
    @Log("从HDFS下载文件")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> downloadFile(
            @RequestParam("hdfsPath") String hdfsPath,
            @RequestParam("localPath") String localPath) {
        
        Map<String, Object> response = new HashMap<>();
        boolean success = hadoopService.downloadFromHDFS(hdfsPath, localPath);
        
        response.put("success", success);
        response.put("message", success ? "文件下载成功" : "文件下载失败");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除 HDFS 文件或目录
     */
    @DeleteMapping("/delete")
    @Log("删除HDFS文件")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam("hdfsPath") String hdfsPath) {
        Map<String, Object> response = new HashMap<>();
        boolean success = hadoopService.deleteFromHDFS(hdfsPath);
        
        response.put("success", success);
        response.put("message", success ? "文件删除成功" : "文件删除失败");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 列出 HDFS 目录下的文件
     */
    @GetMapping("/list")
    @Log("列出HDFS文件")
    public ResponseEntity<Map<String, Object>> listFiles(
            @RequestParam(value = "hdfsPath", defaultValue = "/pig-system") String hdfsPath) {
        
        Map<String, Object> response = new HashMap<>();
        List<String> files = hadoopService.listFiles(hdfsPath);
        
        response.put("success", true);
        response.put("files", files);
        response.put("count", files.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 读取 HDFS 文件内容
     */
    @GetMapping("/read")
    @Log("读取HDFS文件内容")
    public ResponseEntity<Map<String, Object>> readFile(@RequestParam("hdfsPath") String hdfsPath) {
        Map<String, Object> response = new HashMap<>();
        String content = hadoopService.readFileContent(hdfsPath);
        
        response.put("success", true);
        response.put("content", content);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 写入内容到 HDFS 文件
     */
    @PostMapping("/write")
    @Log("写入内容到HDFS")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> writeFile(
            @RequestParam("hdfsPath") String hdfsPath,
            @RequestParam("content") String content) {
        
        Map<String, Object> response = new HashMap<>();
        boolean success = hadoopService.writeToHDFS(hdfsPath, content);
        
        response.put("success", success);
        response.put("message", success ? "写入成功" : "写入失败");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 执行猪数据分析 MapReduce 任务
     */
    @PostMapping("/analyze/pig")
    @Log("执行猪数据分析任务")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> analyzePigData(@RequestParam("inputFile") String inputFile) {
        log.info("开始执行猪数据分析任务，输入文件：{}", inputFile);
        
        try {
            Map<String, Object> result = hadoopService.runPigDataAnalysis(inputFile);
            
            // 构建返回结果
            HadoopJobVO jobVO = new HadoopJobVO();
            jobVO.setJobName((String) result.get("jobName"));
            jobVO.setJobType("pig_analysis");
            jobVO.setInputFile(inputFile);
            jobVO.setOutputPath((String) result.get("outputPath"));
            jobVO.setSuccess((Boolean) result.get("success"));
            jobVO.setResult(result.get("result"));
            jobVO.setError((String) result.get("error"));
            jobVO.setCreateTime(System.currentTimeMillis());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", jobVO.getSuccess());
            response.put("data", jobVO);
            response.put("message", jobVO.getSuccess() ? "分析任务执行成功" : "分析任务执行失败");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("执行猪数据分析任务失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "任务执行失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 执行环境数据分析 MapReduce 任务
     */
    @PostMapping("/analyze/environment")
    @Log("执行环境数据分析任务")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> analyzeEnvironmentData(@RequestParam("inputFile") String inputFile) {
        log.info("开始执行环境数据分析任务，输入文件：{}", inputFile);
        
        try {
            Map<String, Object> result = hadoopService.runEnvironmentDataAnalysis(inputFile);
            
            // 构建返回结果
            HadoopJobVO jobVO = new HadoopJobVO();
            jobVO.setJobName((String) result.get("jobName"));
            jobVO.setJobType("environment_analysis");
            jobVO.setInputFile(inputFile);
            jobVO.setOutputPath((String) result.get("outputPath"));
            jobVO.setSuccess((Boolean) result.get("success"));
            jobVO.setResult(result.get("result"));
            jobVO.setError((String) result.get("error"));
            jobVO.setCreateTime(System.currentTimeMillis());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", jobVO.getSuccess());
            response.put("data", jobVO);
            response.put("message", jobVO.getSuccess() ? "分析任务执行成功" : "分析任务执行失败");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("执行环境数据分析任务失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "任务执行失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量导出数据到 HDFS（用于 MapReduce 分析）
     */
    @PostMapping("/export/pig-data")
    @Log("导出猪数据到HDFS")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> exportPigDataToHDFS() {
        Map<String, Object> response = new HashMap<>();
        
        // 这里可以从数据库查询数据并生成CSV文件上传到HDFS
        // 示例数据格式：猪ID,猪类型,状态,重量,价格
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("猪ID,猪类型,状态,重量,价格\n");
        csvContent.append("1,二元杂交猪,健康,100.5,1500\n");
        csvContent.append("2,三元杂交猪,健康,120.3,1800\n");
        csvContent.append("3,本地猪,健康,90.2,1200\n");
        
        String hdfsPath = "/pig-system/input/pig_data_" + System.currentTimeMillis() + ".csv";
        boolean success = hadoopService.writeToHDFS(hdfsPath, csvContent.toString());
        
        response.put("success", success);
        response.put("hdfsPath", hdfsPath);
        response.put("message", success ? "数据导出成功" : "数据导出失败");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量导出环境数据到 HDFS
     */
    @PostMapping("/export/environment-data")
    @Log("导出环境数据到HDFS")
    @RequiresRole({"ADMIN", "DATA_ANALYST"})
    public ResponseEntity<Map<String, Object>> exportEnvironmentDataToHDFS() {
        Map<String, Object> response = new HashMap<>();
        
        // 示例数据格式：日期,温度,湿度,氨气浓度,二氧化碳浓度
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("日期,温度,湿度,氨气浓度,二氧化碳浓度\n");
        csvContent.append("2024-01-01,25.5,65.0,10.2,450.5\n");
        csvContent.append("2024-01-02,26.0,63.5,9.8,440.0\n");
        csvContent.append("2024-01-03,24.8,67.0,11.0,460.0\n");
        
        String hdfsPath = "/pig-system/input/environment_data_" + System.currentTimeMillis() + ".csv";
        boolean success = hadoopService.writeToHDFS(hdfsPath, csvContent.toString());
        
        response.put("success", success);
        response.put("hdfsPath", hdfsPath);
        response.put("message", success ? "数据导出成功" : "数据导出失败");
        
        return ResponseEntity.ok(response);
    }
}

