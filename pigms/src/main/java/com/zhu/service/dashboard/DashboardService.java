package com.zhu.service.dashboard;

import com.zhu.mapper.EnvironmentDataMapper;
import com.zhu.mapper.PigMapper;
import com.zhu.mapper.PigTypeMapper;
import com.zhu.pojo.EnvironmentData;
import com.zhu.pojo.Pig;
import com.zhu.pojo.PigType;
import com.zhu.service.hadoop.HadoopService;
import com.zhu.vo.DashboardDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 大屏数据服务
 * 聚合Hadoop、Python、数据库的数据，提供给大屏展示
 */
@Slf4j
@Service
public class DashboardService {

    @Autowired
    private HadoopService hadoopService;
    
    @Autowired
    private PigMapper pigMapper;
    
    @Autowired
    private PigTypeMapper pigTypeMapper;
    
    @Autowired
    private EnvironmentDataMapper environmentDataMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;

    /**
     * 获取大屏综合数据
     */
    public DashboardDataVO getDashboardData() {
        DashboardDataVO dashboardData = new DashboardDataVO();
        
        try {
            // 1. 获取实时数据概览
            dashboardData.setRealTimeOverview(getRealTimeOverview());
            
            // 2. 获取Hadoop分析数据
            dashboardData.setHadoopAnalysis(getHadoopAnalysisData());
            
            // 3. 获取Python预测数据
            dashboardData.setPredictionData(getPredictionData());
            
            // 4. 获取环境监测数据
            dashboardData.setEnvironmentData(getEnvironmentData());
            
            // 5. 获取猪场统计数据
            dashboardData.setFarmStatistics(getFarmStatistics());
            
            log.info("大屏数据获取成功");
            
        } catch (Exception e) {
            log.error("获取大屏数据失败", e);
        }
        
        return dashboardData;
    }
    
    /**
     * 获取实时数据概览
     */
    private DashboardDataVO.RealTimeOverview getRealTimeOverview() {
        DashboardDataVO.RealTimeOverview overview = new DashboardDataVO.RealTimeOverview();
        
        try {
            // 查询总猪数
            List<Pig> allPigs = pigMapper.queryAllPig();
            overview.setTotalPigs(allPigs.size());
            
            // 设置健康猪数（假设所有猪都健康，因为表中没有status字段）
            overview.setHealthyPigs(allPigs.size());
            
            // 查询环境数据
            List<EnvironmentData> envDataList = environmentDataMapper.selectAll();
            
            // 统计异常环境数
            long abnormalCount = envDataList.stream()
                    .filter(env -> env.getIsAbnormal() != null && env.getIsAbnormal() == 1)
                    .count();
            overview.setAbnormalEnvironments((int) abnormalCount);
            
            // 计算平均温度和湿度
            if (!envDataList.isEmpty()) {
                Double avgTemp = envDataList.stream()
                        .filter(env -> env.getTemperature() != null)
                        .mapToDouble(env -> env.getTemperature().doubleValue())
                        .average()
                        .orElse(0.0);
                overview.setAvgTemperature(Math.round(avgTemp * 10) / 10.0);
                
                Double avgHum = envDataList.stream()
                        .filter(env -> env.getHumidity() != null)
                        .mapToDouble(env -> env.getHumidity().doubleValue())
                        .average()
                        .orElse(0.0);
                overview.setAvgHumidity(Math.round(avgHum * 10) / 10.0);
            }
            
            // 设置更新时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            overview.setUpdateTime(sdf.format(new Date()));
            
        } catch (Exception e) {
            log.error("获取实时概览数据失败", e);
        }
        
        return overview;
    }
    
    /**
     * 获取Hadoop分析数据（真正调用MapReduce）
     */
    private DashboardDataVO.HadoopAnalysisData getHadoopAnalysisData() {
        DashboardDataVO.HadoopAnalysisData hadoopData = new DashboardDataVO.HadoopAnalysisData();
        
        try {
            // 检查Hadoop连接
            boolean hadoopAvailable = hadoopService.checkConnection();
            
            if (hadoopAvailable) {
                log.info("Hadoop集群可用，开始执行MapReduce分析任务");
                
                // 1. 导出猪数据到HDFS并执行MapReduce分析
                Map<String, Integer> pigTypeDistribution = runPigMapReduceAnalysis();
                hadoopData.setPigTypeDistribution(pigTypeDistribution);
                
                // 2. 导出环境数据到HDFS并执行MapReduce分析
                DashboardDataVO.EnvironmentMetrics metrics = runEnvironmentMapReduceAnalysis();
                hadoopData.setEnvironmentMetrics(metrics);
                
            } else {
                log.warn("Hadoop集群不可用，使用本地数据库统计");
                // 降级方案：使用本地统计
                hadoopData = getLocalHadoopAnalysisData();
            }
            
            // 趋势分析（从数据库获取最新数据）
            DashboardDataVO.TrendAnalysis trendAnalysis = buildTrendAnalysis();
            hadoopData.setTrendAnalysis(trendAnalysis);
            
        } catch (Exception e) {
            log.error("获取Hadoop分析数据失败，降级到本地统计", e);
            // 异常降级方案
            hadoopData = getLocalHadoopAnalysisData();
        }
        
        return hadoopData;
    }
    
    /**
     * 执行猪数据MapReduce分析
     */
    private Map<String, Integer> runPigMapReduceAnalysis() {
        Map<String, Integer> typeDistribution = new HashMap<>();
        
        try {
            // 1. 从数据库导出数据到CSV
            List<Pig> allPigs = pigMapper.queryAllPig();
            List<PigType> pigTypes = pigTypeMapper.queryTypeSum();
            
            // 创建类型ID到名称的映射
            Map<Integer, String> typeIdToNameMap = pigTypes.stream()
                    .collect(Collectors.toMap(PigType::getId, PigType::getPigType));
            
            // 2. 生成CSV内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("猪ID,猪编号,猪名称,猪类型,年龄,性别,出生日期,产地\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Pig pig : allPigs) {
                String typeName = typeIdToNameMap.getOrDefault(pig.getPigtype(), "未知");
                String sexStr = pig.getPinsex() != null ? (pig.getPinsex() == 1 ? "公" : "母") : "未知";
                
                csvContent.append(String.format("%d,%s,%s,%s,%d,%s,%s,%s\n",
                        pig.getId(),
                        pig.getPigisbn() != null ? pig.getPigisbn() : "",
                        pig.getPigname() != null ? pig.getPigname() : "",
                        typeName,
                        pig.getPigage() != null ? pig.getPigage() : 0,
                        sexStr,
                        pig.getManufacturedate() != null ? sdf.format(pig.getManufacturedate()) : "",
                        pig.getPigaddress() != null ? pig.getPigaddress() : ""
                ));
            }
            
            // 3. 上传到HDFS
            String timestamp = String.valueOf(System.currentTimeMillis());
            String hdfsInputPath = "/pig-system/dashboard/pig_data_" + timestamp + ".csv";
            boolean uploadSuccess = hadoopService.writeToHDFS(hdfsInputPath, csvContent.toString());
            
            if (!uploadSuccess) {
                log.error("上传猪数据到HDFS失败");
                return getLocalPigTypeDistribution(allPigs, typeIdToNameMap);
            }
            
            log.info("猪数据已上传到HDFS: {}", hdfsInputPath);
            
            // 4. 执行MapReduce任务
            Map<String, Object> mrResult = hadoopService.runPigDataAnalysis(hdfsInputPath);
            
            if (mrResult != null && Boolean.TRUE.equals(mrResult.get("success"))) {
                log.info("========================================");
                log.info("猪数据 MapReduce 任务执行成功");
                log.info("  - Job ID: {}", mrResult.get("jobId"));
                log.info("  - Tracking URL: {}", mrResult.get("trackingUrl"));
                log.info("  - 耗时: {} ms", mrResult.get("duration"));
                log.info("========================================");
                
                // 5. 解析MapReduce结果
                Object result = mrResult.get("result");
                if (result instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resultMap = (Map<String, Object>) result;
                    
                    for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                        String type = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value instanceof Number) {
                            typeDistribution.put(type, ((Number) value).intValue());
                        } else if (value instanceof String) {
                            try {
                                typeDistribution.put(type, Integer.parseInt((String) value));
                            } catch (NumberFormatException e) {
                                log.warn("无法解析类型统计值: {} = {}", type, value);
                            }
                        }
                    }
                }
                
                log.info("MapReduce结果解析完成，猪类型分布: {}", typeDistribution);
                
                // 6. 清理临时HDFS文件
                try {
                    hadoopService.deleteFromHDFS(hdfsInputPath);
                    if (mrResult.get("outputPath") != null) {
                        hadoopService.deleteFromHDFS((String) mrResult.get("outputPath"));
                    }
                } catch (Exception e) {
                    log.warn("清理HDFS临时文件失败", e);
                }
                
            } else {
                log.error("========================================");
                log.error("猪数据 MapReduce 任务执行失败");
                if (mrResult != null) {
                    log.error("  - 错误信息: {}", mrResult.get("error"));
                    log.error("  - 异常类型: {}", mrResult.get("exceptionType"));
                    if (mrResult.get("jobId") != null) {
                        log.error("  - Job ID: {}", mrResult.get("jobId"));
                    }
                    if (mrResult.get("trackingUrl") != null) {
                        log.error("  - Tracking URL: {}", mrResult.get("trackingUrl"));
                    }
                    if (mrResult.get("stackTrace") != null) {
                        log.error("  - 堆栈跟踪:\n{}", mrResult.get("stackTrace"));
                    }
                } else {
                    log.error("  - MapReduce 任务返回结果为 null");
                }
                log.error("========================================");
                log.warn("降级使用本地数据库统计");
                return getLocalPigTypeDistribution(allPigs, typeIdToNameMap);
            }
            
        } catch (Exception e) {
            log.error("执行猪数据MapReduce分析失败", e);
            // 降级到本地统计
            List<Pig> allPigs = pigMapper.queryAllPig();
            List<PigType> pigTypes = pigTypeMapper.queryTypeSum();
            Map<Integer, String> typeIdToNameMap = pigTypes.stream()
                    .collect(Collectors.toMap(PigType::getId, PigType::getPigType));
            return getLocalPigTypeDistribution(allPigs, typeIdToNameMap);
        }
        
        return typeDistribution;
    }
    
    /**
     * 执行环境数据MapReduce分析
     */
    private DashboardDataVO.EnvironmentMetrics runEnvironmentMapReduceAnalysis() {
        DashboardDataVO.EnvironmentMetrics metrics = new DashboardDataVO.EnvironmentMetrics();
        
        try {
            // 1. 从数据库导出环境数据
            List<EnvironmentData> envDataList = environmentDataMapper.selectAll();
            
            if (envDataList.isEmpty()) {
                log.warn("没有环境数据可供分析");
                return metrics;
            }
            
            // 2. 生成CSV内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("监测点,采集时间,温度,湿度,CO2浓度,氨气浓度,光照强度\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (EnvironmentData data : envDataList) {
                csvContent.append(String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        data.getMonitorPoint() != null ? data.getMonitorPoint() : "未知",
                        data.getCollectTime() != null ? sdf.format(data.getCollectTime()) : "",
                        data.getTemperature() != null ? data.getTemperature().doubleValue() : 0.0,
                        data.getHumidity() != null ? data.getHumidity().doubleValue() : 0.0,
                        data.getCo2Concentration() != null ? data.getCo2Concentration().doubleValue() : 0.0,
                        data.getNh3Concentration() != null ? data.getNh3Concentration().doubleValue() : 0.0,
                        data.getLightIntensity() != null ? data.getLightIntensity().doubleValue() : 0.0
                ));
            }
            
            // 3. 上传到HDFS
            String timestamp = String.valueOf(System.currentTimeMillis());
            String hdfsInputPath = "/pig-system/dashboard/env_data_" + timestamp + ".csv";
            boolean uploadSuccess = hadoopService.writeToHDFS(hdfsInputPath, csvContent.toString());
            
            if (!uploadSuccess) {
                log.error("上传环境数据到HDFS失败");
                return getLocalEnvironmentMetrics(envDataList);
            }
            
            log.info("环境数据已上传到HDFS: {}", hdfsInputPath);
            
            // 4. 执行MapReduce任务
            Map<String, Object> mrResult = hadoopService.runEnvironmentDataAnalysis(hdfsInputPath);
            
            if (mrResult != null && Boolean.TRUE.equals(mrResult.get("success"))) {
                log.info("========================================");
                log.info("环境数据 MapReduce 任务执行成功");
                log.info("  - Job ID: {}", mrResult.get("jobId"));
                log.info("  - Tracking URL: {}", mrResult.get("trackingUrl"));
                log.info("  - 耗时: {} ms", mrResult.get("duration"));
                log.info("========================================");
                
                // 5. 解析MapReduce结果
                Object result = mrResult.get("result");
                if (result instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resultMap = (Map<String, Object>) result;
                    
                    // 解析温度统计
                    if (resultMap.containsKey("temperature")) {
                        metrics.setTemperature(parseMetricStat(resultMap.get("temperature")));
                    }
                    // 解析湿度统计
                    if (resultMap.containsKey("humidity")) {
                        metrics.setHumidity(parseMetricStat(resultMap.get("humidity")));
                    }
                    // 解析CO2统计
                    if (resultMap.containsKey("co2")) {
                        metrics.setCo2(parseMetricStat(resultMap.get("co2")));
                    }
                    // 解析氨气统计
                    if (resultMap.containsKey("ammonia")) {
                        metrics.setAmmonia(parseMetricStat(resultMap.get("ammonia")));
                    }
                }
                
                log.info("环境数据MapReduce结果解析完成");
                
                // 6. 清理临时HDFS文件
                try {
                    hadoopService.deleteFromHDFS(hdfsInputPath);
                    if (mrResult.get("outputPath") != null) {
                        hadoopService.deleteFromHDFS((String) mrResult.get("outputPath"));
                    }
                } catch (Exception e) {
                    log.warn("清理HDFS临时文件失败", e);
                }
                
            } else {
                log.error("========================================");
                log.error("环境数据 MapReduce 任务执行失败");
                if (mrResult != null) {
                    log.error("  - 错误信息: {}", mrResult.get("error"));
                    log.error("  - 异常类型: {}", mrResult.get("exceptionType"));
                    if (mrResult.get("jobId") != null) {
                        log.error("  - Job ID: {}", mrResult.get("jobId"));
                    }
                    if (mrResult.get("trackingUrl") != null) {
                        log.error("  - Tracking URL: {}", mrResult.get("trackingUrl"));
                    }
                    if (mrResult.get("stackTrace") != null) {
                        log.error("  - 堆栈跟踪:\n{}", mrResult.get("stackTrace"));
                    }
                } else {
                    log.error("  - MapReduce 任务返回结果为 null");
                }
                log.error("========================================");
                log.warn("降级使用本地数据库统计");
                return getLocalEnvironmentMetrics(envDataList);
            }
            
        } catch (Exception e) {
            log.error("执行环境数据MapReduce分析失败", e);
            // 降级到本地统计
            List<EnvironmentData> envDataList = environmentDataMapper.selectAll();
            return getLocalEnvironmentMetrics(envDataList);
        }
        
        return metrics;
    }
    
    /**
     * 解析MapReduce返回的指标统计
     */
    private DashboardDataVO.MetricStat parseMetricStat(Object obj) {
        DashboardDataVO.MetricStat stat = new DashboardDataVO.MetricStat();
        
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            
            if (map.containsKey("average")) {
                stat.setAverage(((Number) map.get("average")).doubleValue());
            }
            if (map.containsKey("max")) {
                stat.setMax(((Number) map.get("max")).doubleValue());
            }
            if (map.containsKey("min")) {
                stat.setMin(((Number) map.get("min")).doubleValue());
            }
            if (map.containsKey("count")) {
                stat.setCount(((Number) map.get("count")).intValue());
            }
            if (map.containsKey("standardRate")) {
                stat.setStandardRate(((Number) map.get("standardRate")).doubleValue());
            }
        }
        
        return stat;
    }
    
    /**
     * 本地猪类型分布统计（降级方案）
     */
    private Map<String, Integer> getLocalPigTypeDistribution(List<Pig> allPigs, Map<Integer, String> typeIdToNameMap) {
        return allPigs.stream()
                .collect(Collectors.groupingBy(
                        pig -> {
                            Integer typeId = pig.getPigtype();
                            return typeIdToNameMap.getOrDefault(typeId, "未知");
                        },
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
    
    /**
     * 本地环境指标统计（降级方案）
     */
    private DashboardDataVO.EnvironmentMetrics getLocalEnvironmentMetrics(List<EnvironmentData> envDataList) {
        DashboardDataVO.EnvironmentMetrics metrics = new DashboardDataVO.EnvironmentMetrics();
        
        if (!envDataList.isEmpty()) {
            metrics.setTemperature(calculateMetricStat(envDataList, "temperature", 18.0, 28.0));
            metrics.setHumidity(calculateMetricStat(envDataList, "humidity", 50.0, 70.0));
            metrics.setAmmonia(calculateMetricStat(envDataList, "ammonia", 0.0, 25.0));
            metrics.setCo2(calculateMetricStat(envDataList, "co2", 0.0, 600.0));
        }
        
        return metrics;
    }
    
    /**
     * 本地Hadoop分析数据（完整降级方案）
     */
    private DashboardDataVO.HadoopAnalysisData getLocalHadoopAnalysisData() {
        DashboardDataVO.HadoopAnalysisData hadoopData = new DashboardDataVO.HadoopAnalysisData();
        
        try {
            // 获取所有猪类型
            List<PigType> pigTypes = pigTypeMapper.queryTypeSum();
            Map<Integer, String> typeIdToNameMap = pigTypes.stream()
                    .collect(Collectors.toMap(PigType::getId, PigType::getPigType));
            
            // 获取猪类型分布
            List<Pig> allPigs = pigMapper.queryAllPig();
            Map<String, Integer> typeDistribution = getLocalPigTypeDistribution(allPigs, typeIdToNameMap);
            hadoopData.setPigTypeDistribution(typeDistribution);
            
            // 获取环境指标统计
            List<EnvironmentData> envDataList = environmentDataMapper.selectAll();
            DashboardDataVO.EnvironmentMetrics metrics = getLocalEnvironmentMetrics(envDataList);
            hadoopData.setEnvironmentMetrics(metrics);
            
        } catch (Exception e) {
            log.error("本地Hadoop分析数据统计失败", e);
        }
        
        return hadoopData;
    }
    
    /**
     * 构建趋势分析数据
     */
    private DashboardDataVO.TrendAnalysis buildTrendAnalysis() {
        DashboardDataVO.TrendAnalysis trendAnalysis = new DashboardDataVO.TrendAnalysis();
        
        try {
            List<EnvironmentData> envDataList = environmentDataMapper.selectAll();
            
            // 取最近20条数据
            List<EnvironmentData> recentData = envDataList.stream()
                    .filter(data -> data.getCollectTime() != null)
                    .sorted(Comparator.comparing(EnvironmentData::getCollectTime).reversed())
                    .limit(20)
                    .sorted(Comparator.comparing(EnvironmentData::getCollectTime))
                    .collect(Collectors.toList());
            
            trendAnalysis.setTemperatureTrend(buildTimeSeries(recentData, "temperature"));
            trendAnalysis.setHumidityTrend(buildTimeSeries(recentData, "humidity"));
            
        } catch (Exception e) {
            log.error("构建趋势分析数据失败", e);
        }
        
        return trendAnalysis;
    }
    
    /**
     * 计算指标统计
     */
    private DashboardDataVO.MetricStat calculateMetricStat(
            List<EnvironmentData> dataList, String metricType, Double minStandard, Double maxStandard) {
        
        DashboardDataVO.MetricStat stat = new DashboardDataVO.MetricStat();
        
        List<Double> values = new ArrayList<>();
        int standardCount = 0;
        
        for (EnvironmentData data : dataList) {
            BigDecimal value = null;
            switch (metricType) {
                case "temperature":
                    value = data.getTemperature();
                    break;
                case "humidity":
                    value = data.getHumidity();
                    break;
                case "ammonia":
                    value = data.getNh3Concentration();
                    break;
                case "co2":
                    value = data.getCo2Concentration();
                    break;
            }
            
            if (value != null) {
                double doubleValue = value.doubleValue();
                values.add(doubleValue);
                if (doubleValue >= minStandard && doubleValue <= maxStandard) {
                    standardCount++;
                }
            }
        }
        
        if (!values.isEmpty()) {
            stat.setAverage(Math.round(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0) * 10) / 10.0);
            stat.setMax(values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
            stat.setMin(values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
            stat.setCount(values.size());
            stat.setStandardRate(Math.round((double) standardCount / values.size() * 1000) / 10.0);
        }
        
        return stat;
    }
    
    /**
     * 构建时间序列数据
     */
    private List<DashboardDataVO.TimeSeriesData> buildTimeSeries(List<EnvironmentData> dataList, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        
        return dataList.stream()
                .map(data -> {
                    DashboardDataVO.TimeSeriesData ts = new DashboardDataVO.TimeSeriesData();
                    
                    // 格式化时间
                    if (data.getCollectTime() != null) {
                        ts.setTime(sdf.format(data.getCollectTime()));
                    }
                    
                    // 获取数值
                    BigDecimal value = null;
                    if ("temperature".equals(type)) {
                        value = data.getTemperature();
                    } else if ("humidity".equals(type)) {
                        value = data.getHumidity();
                    }
                    
                    ts.setValue(value != null ? value.doubleValue() : null);
                    return ts;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取Python预测数据
     */
    private DashboardDataVO.PredictionData getPredictionData() {
        DashboardDataVO.PredictionData predictionData = new DashboardDataVO.PredictionData();
        
        try {
            // 1. 生长预测
            predictionData.setGrowthPrediction(getGrowthPrediction());
            
            // 2. 环境质量评价
            predictionData.setEnvironmentQuality(getEnvironmentQuality());
            
            // 3. 疾病风险预测
            predictionData.setDiseaseRisk(getDiseaseRisk());
            
        } catch (Exception e) {
            log.error("获取Python预测数据失败，使用模拟数据", e);
            // 使用模拟数据
            predictionData.setGrowthPrediction(getMockGrowthPrediction());
            predictionData.setEnvironmentQuality(getMockEnvironmentQuality());
            predictionData.setDiseaseRisk(getMockDiseaseRisk());
        }
        
        return predictionData;
    }
    
    /**
     * 获取生长预测（调用Python服务）
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DashboardDataVO.GrowthPrediction getGrowthPrediction() {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model_type", "GROWTH");
            
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("age", 100);
            inputData.put("feed", 2.5);
            inputData.put("breed", "三元杂交猪");
            inputData.put("sex", "公");
            requestData.put("input_data", inputData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/predict", request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                Map<String, Object> result = (Map<String, Object>) responseBody.get("data");
                
                if (result != null) {
                    DashboardDataVO.GrowthPrediction prediction = new DashboardDataVO.GrowthPrediction();
                    prediction.setPredictedWeight(((Number) result.get("predicted_weight")).doubleValue());
                    prediction.setGrowthRate(((Number) result.get("growth_rate")).doubleValue());
                    prediction.setAccuracy(((Number) result.get("accuracy")).doubleValue());
                    prediction.setTrend("上升");
                    return prediction;
                }
            }
            
        } catch (Exception e) {
            log.warn("调用Python生长预测失败，使用模拟数据", e);
        }
        
        return getMockGrowthPrediction();
    }
    
    /**
     * 获取环境质量评价
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DashboardDataVO.EnvironmentQuality getEnvironmentQuality() {
        try {
            // 获取最新环境数据
            List<EnvironmentData> envDataList = environmentDataMapper.selectLatestByMonitorPoint(1);
            if (envDataList == null || envDataList.isEmpty()) {
                return getMockEnvironmentQuality();
            }
            
            EnvironmentData latestData = envDataList.get(0);
            
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model_type", "ENVIRONMENT");
            
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("temperature", latestData.getTemperature() != null ? latestData.getTemperature().doubleValue() : 25.0);
            inputData.put("humidity", latestData.getHumidity() != null ? latestData.getHumidity().doubleValue() : 65.0);
            inputData.put("co2", latestData.getCo2Concentration() != null ? latestData.getCo2Concentration().doubleValue() : 500.0);
            inputData.put("nh3", latestData.getNh3Concentration() != null ? latestData.getNh3Concentration().doubleValue() : 20.0);
            inputData.put("light", latestData.getLightIntensity() != null ? latestData.getLightIntensity().doubleValue() : 300.0);
            requestData.put("input_data", inputData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/predict", request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                Map<String, Object> result = (Map<String, Object>) responseBody.get("data");
                
                if (result != null) {
                    DashboardDataVO.EnvironmentQuality quality = new DashboardDataVO.EnvironmentQuality();
                    quality.setQualityLevel((String) result.get("quality_level"));
                    quality.setQuality((String) result.get("quality"));
                    quality.setScore(((Number) result.get("score")).intValue());
                    quality.setSuggestions((String) result.get("suggestions"));
                    return quality;
                }
            }
            
        } catch (Exception e) {
            log.warn("调用Python环境质量评价失败，使用模拟数据", e);
        }
        
        return getMockEnvironmentQuality();
    }
    
    /**
     * 获取疾病风险预测
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DashboardDataVO.DiseaseRisk getDiseaseRisk() {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model_type", "DISEASE");
            
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("temperature", 25.0);
            inputData.put("humidity", 65.0);
            inputData.put("density", 10);
            inputData.put("age", 60);
            inputData.put("vaccinated", true);
            requestData.put("input_data", inputData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    pythonServiceUrl + "/predict", request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                Map<String, Object> result = (Map<String, Object>) responseBody.get("data");
                
                if (result != null) {
                    DashboardDataVO.DiseaseRisk risk = new DashboardDataVO.DiseaseRisk();
                    risk.setRiskLevel((String) result.get("risk_level"));
                    risk.setRiskProbability(((Number) result.get("risk_probability")).doubleValue());
                    risk.setPreventionAdvice((String) result.get("prevention_advice"));
                    return risk;
                }
            }
            
        } catch (Exception e) {
            log.warn("调用Python疾病风险预测失败，使用模拟数据", e);
        }
        
        return getMockDiseaseRisk();
    }
    
    /**
     * 模拟生长预测数据
     */
    private DashboardDataVO.GrowthPrediction getMockGrowthPrediction() {
        DashboardDataVO.GrowthPrediction prediction = new DashboardDataVO.GrowthPrediction();
        prediction.setPredictedWeight(115.5);
        prediction.setGrowthRate(0.95);
        prediction.setAccuracy(0.925);
        prediction.setTrend("上升");
        return prediction;
    }
    
    /**
     * 模拟环境质量数据
     */
    private DashboardDataVO.EnvironmentQuality getMockEnvironmentQuality() {
        DashboardDataVO.EnvironmentQuality quality = new DashboardDataVO.EnvironmentQuality();
        quality.setQualityLevel("II");
        quality.setQuality("良好");
        quality.setScore(85);
        quality.setSuggestions("环境良好，建议保持");
        return quality;
    }
    
    /**
     * 模拟疾病风险数据
     */
    private DashboardDataVO.DiseaseRisk getMockDiseaseRisk() {
        DashboardDataVO.DiseaseRisk risk = new DashboardDataVO.DiseaseRisk();
        risk.setRiskLevel("低");
        risk.setRiskProbability(0.15);
        risk.setPreventionAdvice("风险较低，注意日常卫生和防疫");
        return risk;
    }
    
    /**
     * 获取环境监测数据
     */
    private DashboardDataVO.EnvironmentData getEnvironmentData() {
        DashboardDataVO.EnvironmentData envData = new DashboardDataVO.EnvironmentData();
        
        try {
            // 获取最新的监测点数据
            List<EnvironmentData> envDataList = environmentDataMapper.selectLatestByMonitorPoint(10);
            
            List<DashboardDataVO.MonitorPoint> monitorPoints = envDataList.stream()
                    .map(data -> {
                        DashboardDataVO.MonitorPoint point = new DashboardDataVO.MonitorPoint();
                        point.setName(data.getMonitorPoint());
                        point.setLocation(data.getMonitorLocation());
                        point.setTemperature(data.getTemperature() != null ? data.getTemperature().doubleValue() : null);
                        point.setHumidity(data.getHumidity() != null ? data.getHumidity().doubleValue() : null);
                        point.setCo2(data.getCo2Concentration() != null ? data.getCo2Concentration().doubleValue() : null);
                        point.setNh3(data.getNh3Concentration() != null ? data.getNh3Concentration().doubleValue() : null);
                        point.setIsAbnormal(data.getIsAbnormal() != null && data.getIsAbnormal() == 1);
                        point.setStatus(data.getIsAbnormal() != null && data.getIsAbnormal() == 1 ? "异常" : "正常");
                        return point;
                    })
                    .collect(Collectors.toList());
            
            envData.setMonitorPoints(monitorPoints);
            
            // 异常统计
            List<EnvironmentData> allEnvData = environmentDataMapper.selectAll();
            Map<String, Integer> abnormalStats = new HashMap<>();
            
            long tempAbnormal = allEnvData.stream()
                    .filter(d -> d.getTemperature() != null && 
                            (d.getTemperature().compareTo(new BigDecimal("18")) < 0 || 
                             d.getTemperature().compareTo(new BigDecimal("28")) > 0))
                    .count();
            abnormalStats.put("温度异常", (int) tempAbnormal);
            
            long humAbnormal = allEnvData.stream()
                    .filter(d -> d.getHumidity() != null && 
                            (d.getHumidity().compareTo(new BigDecimal("50")) < 0 || 
                             d.getHumidity().compareTo(new BigDecimal("70")) > 0))
                    .count();
            abnormalStats.put("湿度异常", (int) humAbnormal);
            
            long co2Abnormal = allEnvData.stream()
                    .filter(d -> d.getCo2Concentration() != null && 
                            d.getCo2Concentration().compareTo(new BigDecimal("600")) > 0)
                    .count();
            abnormalStats.put("CO2超标", (int) co2Abnormal);
            
            long nh3Abnormal = allEnvData.stream()
                    .filter(d -> d.getNh3Concentration() != null && 
                            d.getNh3Concentration().compareTo(new BigDecimal("25")) > 0)
                    .count();
            abnormalStats.put("氨气超标", (int) nh3Abnormal);
            
            envData.setAbnormalStats(abnormalStats);
            
        } catch (Exception e) {
            log.error("获取环境监测数据失败", e);
        }
        
        return envData;
    }
    
    /**
     * 获取猪场统计数据
     */
    private DashboardDataVO.FarmStatistics getFarmStatistics() {
        DashboardDataVO.FarmStatistics statistics = new DashboardDataVO.FarmStatistics();
        
        try {
            List<Pig> allPigs = pigMapper.queryAllPig();
            List<PigType> pigTypes = pigTypeMapper.queryTypeSum();
            
            // 创建类型ID到名称的映射
            Map<Integer, String> typeIdToNameMap = pigTypes.stream()
                    .collect(Collectors.toMap(PigType::getId, PigType::getPigType));
            
            // 猪类型统计
            Map<String, Long> typeCountMap = allPigs.stream()
                    .collect(Collectors.groupingBy(
                            pig -> {
                                Integer typeId = pig.getPigtype();
                                return typeIdToNameMap.getOrDefault(typeId, "未知");
                            },
                            Collectors.counting()
                    ));
            
            List<DashboardDataVO.PigTypeCount> pigTypeCounts = typeCountMap.entrySet().stream()
                    .map(entry -> {
                        DashboardDataVO.PigTypeCount count = new DashboardDataVO.PigTypeCount();
                        count.setTypeName(entry.getKey());
                        count.setCount(entry.getValue().intValue());
                        count.setPercentage(allPigs.isEmpty() ? 0.0 : 
                                Math.round((double) entry.getValue() / allPigs.size() * 1000) / 10.0);
                        return count;
                    })
                    .sorted(Comparator.comparing(DashboardDataVO.PigTypeCount::getCount).reversed())
                    .collect(Collectors.toList());
            
            statistics.setPigTypeCounts(pigTypeCounts);
            
            // 状态分布（原系统没有状态字段，返回空Map）
            Map<String, Integer> statusDistribution = new HashMap<>();
            statusDistribution.put("全部", allPigs.size());
            statistics.setStatusDistribution(statusDistribution);
            
        } catch (Exception e) {
            log.error("获取猪场统计数据失败", e);
        }
        
        return statistics;
    }
}
