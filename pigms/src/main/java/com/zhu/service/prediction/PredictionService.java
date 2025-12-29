package com.zhu.service.prediction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhu.mapper.PredictionRecordMapper;
import com.zhu.pojo.Model;
import com.zhu.pojo.PredictionRecord;
import com.zhu.service.model.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预测分析 Service
 */
@Service
public class PredictionService {

    @Autowired
    private PredictionRecordMapper predictionRecordMapper;

    @Autowired
    private ModelService modelService;

    @Autowired
    private RestTemplate restTemplate;

    // Python 服务地址（可在 application.yml 配置）
    @Value("${python.service.url:http://localhost:5000}")
    private String pythonServiceUrl;

    /**
     * 执行预测
     * @param modelId 模型ID
     * @param inputData 输入数据（JSON格式）
     * @param userId 用户ID
     * @return 预测结果
     */
    public PredictionRecord predict(Integer modelId, Map<String, Object> inputData, Integer userId) {
        long startTime = System.currentTimeMillis();
        
        // 获取模型信息
        Model model = modelService.selectByPrimaryKey(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        // 创建预测记录
        PredictionRecord record = new PredictionRecord();
        record.setModelId(modelId);
        record.setModelName(model.getModelName());
        record.setInputData(JSON.toJSONString(inputData));
        record.setUserId(userId);
        record.setCreateTime(new Date());

        try {
            // 调用 Python 服务进行预测
            Map<String, Object> result = callPythonService(model, inputData);
            
            // 保存结果
            record.setOutputData(JSON.toJSONString(result));
            
            // 获取精度（如果有）
            if (result.containsKey("accuracy")) {
                record.setAccuracy(new BigDecimal(result.get("accuracy").toString()));
            } else {
                record.setAccuracy(model.getAccuracy());
            }
            
            // 计算执行时间
            long executeTime = System.currentTimeMillis() - startTime;
            record.setExecuteTime(executeTime);
            
            // 保存记录
            predictionRecordMapper.insert(record);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("预测失败：" + e.getMessage());
        }

        return record;
    }

    /**
     * 调用 Python 服务
     */
    private Map<String, Object> callPythonService(Model model, Map<String, Object> inputData) {
        try {
            // 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model_type", model.getModelType());
            requestBody.put("model_path", model.getModelPath());
            requestBody.put("input_data", inputData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                pythonServiceUrl + "/predict",
                request,
                String.class
            );

            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(response.getBody());
            if (jsonResponse.getInteger("code") == 200) {
                return jsonResponse.getJSONObject("data");
            } else {
                throw new RuntimeException(jsonResponse.getString("message"));
            }

        } catch (Exception e) {
            // 如果 Python 服务不可用，返回模拟数据（用于演示）
            return getMockPredictionResult(model, inputData);
        }
    }

    /**
     * 获取模拟预测结果（用于演示，当 Python 服务不可用时）
     */
    private Map<String, Object> getMockPredictionResult(Model model, Map<String, Object> inputData) {
        Map<String, Object> result = new HashMap<>();
        
        switch (model.getModelType()) {
            case "GROWTH":
                // 生猪体重预测
                result.put("predicted_weight", 85.5 + Math.random() * 10);
                result.put("growth_rate", 0.75 + Math.random() * 0.2);
                break;
            case "ENVIRONMENT":
                // 环境质量评价
                result.put("quality_level", "I");
                result.put("score", 85 + Math.random() * 10);
                result.put("suggestions", "环境良好，建议保持");
                break;
            case "DISEASE":
                // 疾病风险预测
                result.put("risk_level", "低");
                result.put("risk_probability", 0.15 + Math.random() * 0.1);
                result.put("prevention_advice", "注意定期消毒");
                break;
        }
        
        result.put("accuracy", model.getAccuracy());
        result.put("note", "这是模拟数据，实际应用需连接Python服务");
        
        return result;
    }

    /**
     * 查询预测记录
     */
    public List<PredictionRecord> selectAll() {
        return predictionRecordMapper.selectAll();
    }

    /**
     * 按用户查询预测记录
     */
    public List<PredictionRecord> selectByUserId(Integer userId) {
        return predictionRecordMapper.selectByUserId(userId);
    }

    /**
     * 按模型查询预测记录
     */
    public List<PredictionRecord> selectByModelId(Integer modelId) {
        return predictionRecordMapper.selectByModelId(modelId);
    }
}



