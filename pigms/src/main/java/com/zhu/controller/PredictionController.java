package com.zhu.controller;

import com.zhu.annotation.Log;
import com.zhu.pojo.Model;
import com.zhu.pojo.PredictionRecord;
import com.zhu.service.model.ModelService;
import com.zhu.service.prediction.PredictionService;
import com.zhu.utils.json.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 预测分析 Controller
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/prediction")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private ModelService modelService;

    /**
     * 获取所有模型
     */
    @GetMapping("/models")
    public JSONData getAllModels() {
        List<Model> list = modelService.selectAll();
        return JSONData.buildSuccess(list);
    }

    /**
     * 按类型获取模型
     */
    @GetMapping("/models/type/{type}")
    public JSONData getModelsByType(@PathVariable String type) {
        List<Model> list = modelService.selectByType(type);
        return JSONData.buildSuccess(list);
    }

    /**
     * 获取公开模型
     */
    @GetMapping("/models/public")
    public JSONData getPublicModels() {
        List<Model> list = modelService.selectPublicModels();
        return JSONData.buildSuccess(list);
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/models/{id}")
    public JSONData getModelById(@PathVariable Integer id) {
        Model model = modelService.selectByPrimaryKey(id);
        return JSONData.buildSuccess(model);
    }

    /**
     * 执行预测 - 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @Log("执行预测分析")
    @PostMapping("/predict")
    public JSONData predict(
            @RequestParam Integer modelId,
            @RequestBody Map<String, Object> inputData,
            @RequestParam(required = false, defaultValue = "1") Integer userId) {
        
        try {
            PredictionRecord record = predictionService.predict(modelId, inputData, userId);
            return JSONData.buildSuccess(record);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONData.buildError("预测失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有预测记录 - 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @GetMapping("/records")
    public JSONData getAllPredictionRecords() {
        List<PredictionRecord> list = predictionService.selectAll();
        return JSONData.buildSuccess(list);
    }

    /**
     * 按用户查询预测记录 - 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @GetMapping("/records/user/{userId}")
    public JSONData getPredictionRecordsByUser(@PathVariable Integer userId) {
        List<PredictionRecord> list = predictionService.selectByUserId(userId);
        return JSONData.buildSuccess(list);
    }

    /**
     * 按模型查询预测记录 - 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @GetMapping("/records/model/{modelId}")
    public JSONData getPredictionRecordsByModel(@PathVariable Integer modelId) {
        List<PredictionRecord> list = predictionService.selectByModelId(modelId);
        return JSONData.buildSuccess(list);
    }

    /**
     * 添加模型 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Log("添加预测模型")
    @PostMapping("/models")
    public JSONData addModel(@RequestBody Model model) {
        modelService.insert(model);
        return JSONData.buildSuccess("添加成功");
    }

    /**
     * 更新模型 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Log("更新预测模型")
    @PutMapping("/models/{id}")
    public JSONData updateModel(@PathVariable Integer id, @RequestBody Model model) {
        model.setId(id);
        modelService.updateByPrimaryKey(model);
        return JSONData.buildSuccess("更新成功");
    }

    /**
     * 删除模型 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除预测模型")
    @DeleteMapping("/models/{id}")
    public JSONData deleteModel(@PathVariable Integer id) {
        modelService.deleteByPrimaryKey(id);
        return JSONData.buildSuccess("删除成功");
    }
}



