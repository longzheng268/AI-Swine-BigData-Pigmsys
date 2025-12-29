package com.zhu.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 大屏数据视图对象
 * 整合Hadoop和Python分析结果的数据
 */
@Data
public class DashboardDataVO {
    
    // ===== 实时数据概览 =====
    private RealTimeOverview realTimeOverview;
    
    // ===== Hadoop分析结果 =====
    private HadoopAnalysisData hadoopAnalysis;
    
    // ===== Python预测结果 =====
    private PredictionData predictionData;
    
    // ===== 环境监测数据 =====
    private EnvironmentData environmentData;
    
    // ===== 猪场统计数据 =====
    private FarmStatistics farmStatistics;
    
    /**
     * 实时数据概览
     */
    @Data
    public static class RealTimeOverview {
        private Integer totalPigs;           // 总猪数
        private Integer healthyPigs;         // 健康猪数
        private Integer abnormalEnvironments; // 异常环境数
        private Double avgTemperature;       // 平均温度
        private Double avgHumidity;          // 平均湿度
        private String updateTime;           // 更新时间
    }
    
    /**
     * Hadoop分析数据
     */
    @Data
    public static class HadoopAnalysisData {
        // 猪数据分析结果 - 按类型统计
        private Map<String, Integer> pigTypeDistribution;
        
        // 环境数据分析结果 - 统计指标
        private EnvironmentMetrics environmentMetrics;
        
        // 趋势分析
        private TrendAnalysis trendAnalysis;
    }
    
    /**
     * 环境指标统计
     */
    @Data
    public static class EnvironmentMetrics {
        private MetricStat temperature;   // 温度统计
        private MetricStat humidity;      // 湿度统计
        private MetricStat ammonia;       // 氨气统计
        private MetricStat co2;           // CO2统计
    }
    
    /**
     * 单个指标统计
     */
    @Data
    public static class MetricStat {
        private Double average;
        private Double max;
        private Double min;
        private Integer count;
        private Double standardRate; // 达标率
    }
    
    /**
     * 趋势分析
     */
    @Data
    public static class TrendAnalysis {
        private List<TimeSeriesData> temperatureTrend;
        private List<TimeSeriesData> humidityTrend;
        private List<TimeSeriesData> growthTrend;
    }
    
    /**
     * 时间序列数据
     */
    @Data
    public static class TimeSeriesData {
        private String time;
        private Double value;
    }
    
    /**
     * Python预测数据
     */
    @Data
    public static class PredictionData {
        private GrowthPrediction growthPrediction;
        private EnvironmentQuality environmentQuality;
        private DiseaseRisk diseaseRisk;
    }
    
    /**
     * 生长预测
     */
    @Data
    public static class GrowthPrediction {
        private Double predictedWeight;
        private Double growthRate;
        private Double accuracy;
        private String trend;  // "上升" or "下降" or "稳定"
    }
    
    /**
     * 环境质量
     */
    @Data
    public static class EnvironmentQuality {
        private String qualityLevel;  // I, II, III, IV
        private String quality;       // 优秀、良好、一般、较差
        private Integer score;
        private String suggestions;
    }
    
    /**
     * 疾病风险
     */
    @Data
    public static class DiseaseRisk {
        private String riskLevel;     // 低、中、较高、高
        private Double riskProbability;
        private String preventionAdvice;
    }
    
    /**
     * 环境监测数据
     */
    @Data
    public static class EnvironmentData {
        private List<MonitorPoint> monitorPoints;
        private Map<String, Integer> abnormalStats; // 异常统计
    }
    
    /**
     * 监测点
     */
    @Data
    public static class MonitorPoint {
        private String name;
        private String location;
        private Double temperature;
        private Double humidity;
        private Double co2;
        private Double nh3;
        private Boolean isAbnormal;
        private String status;  // "正常" or "警告" or "异常"
    }
    
    /**
     * 猪场统计
     */
    @Data
    public static class FarmStatistics {
        private List<PigTypeCount> pigTypeCounts;
        private Map<String, Integer> statusDistribution; // 状态分布
        private List<RegionData> regionDistribution;     // 区域分布
    }
    
    /**
     * 猪类型统计
     */
    @Data
    public static class PigTypeCount {
        private String typeName;
        private Integer count;
        private Double percentage;
    }
    
    /**
     * 区域数据
     */
    @Data
    public static class RegionData {
        private String region;
        private Integer count;
        private Double avgWeight;
    }
}

