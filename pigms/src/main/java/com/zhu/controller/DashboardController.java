package com.zhu.controller;

import com.zhu.annotation.Log;
import com.zhu.service.dashboard.DashboardService;
import com.zhu.vo.DashboardDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 大屏数据控制器
 * 提供大屏所需的聚合数据接口
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取大屏综合数据
     * 整合Hadoop、Python、数据库的所有数据
     */
    @GetMapping("/data")
    @Log("获取大屏数据")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        log.info("开始获取大屏数据");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            DashboardDataVO dashboardData = dashboardService.getDashboardData();
            
            response.put("success", true);
            response.put("data", dashboardData);
            response.put("message", "数据获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取大屏数据失败", e);
            response.put("success", false);
            response.put("message", "数据获取失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取实时概览数据
     */
    @GetMapping("/overview")
    @Log("获取实时概览")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DashboardDataVO dashboardData = dashboardService.getDashboardData();
            
            response.put("success", true);
            response.put("data", dashboardData.getRealTimeOverview());
            response.put("message", "获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取实时概览失败", e);
            response.put("success", false);
            response.put("message", "获取失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取Hadoop分析结果
     */
    @GetMapping("/hadoop-analysis")
    @Log("获取Hadoop分析结果")
    public ResponseEntity<Map<String, Object>> getHadoopAnalysis() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DashboardDataVO dashboardData = dashboardService.getDashboardData();
            
            response.put("success", true);
            response.put("data", dashboardData.getHadoopAnalysis());
            response.put("message", "获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取Hadoop分析结果失败", e);
            response.put("success", false);
            response.put("message", "获取失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取Python预测结果
     */
    @GetMapping("/prediction")
    @Log("获取预测结果")
    public ResponseEntity<Map<String, Object>> getPrediction() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            DashboardDataVO dashboardData = dashboardService.getDashboardData();
            
            response.put("success", true);
            response.put("data", dashboardData.getPredictionData());
            response.put("message", "获取成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取预测结果失败", e);
            response.put("success", false);
            response.put("message", "获取失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "大屏服务运行正常");
        return ResponseEntity.ok(response);
    }
}

