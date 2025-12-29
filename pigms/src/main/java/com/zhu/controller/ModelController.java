package com.zhu.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhu.annotation.Log;
import com.zhu.pojo.Model;
import com.zhu.service.model.ModelService;
import com.zhu.utils.json.JSONData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/model")
@CrossOrigin
public class ModelController {

    @Autowired
    private ModelService modelService;

    /**
     * 获取模型列表（分页）
     */
    @GetMapping("/list")
    @Log("查询模型列表")
    public JSONData getModelList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String modelType,
            @RequestParam(required = false) String status) {
        
        log.info("查询模型列表 - 页码:{}, 每页:{}, 名称:{}, 类型:{}, 状态:{}", 
                page, size, modelName, modelType, status);
        
        PageHelper.startPage(page, size);
        List<Model> models = modelService.selectByCondition(modelName, modelType, status);
        PageInfo<Model> pageInfo = new PageInfo<>(models);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("pages", pageInfo.getPages());
        
        return JSONData.buildSuccess(result);
    }

    /**
     * 获取我的模型列表
     */
    @GetMapping("/my-models")
    @Log("查询我的模型")
    public JSONData getMyModels(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 从token中获取当前用户ID（这里需要实现获取当前用户的逻辑）
        // Integer userId = getCurrentUserId();
        
        PageHelper.startPage(page, size);
        // List<Model> models = modelService.selectByCreatorId(userId);
        List<Model> models = modelService.selectAll(); // 临时实现
        PageInfo<Model> pageInfo = new PageInfo<>(models);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        
        return JSONData.buildSuccess(result);
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/{id}")
    @Log("查看模型详情")
    public JSONData getModelById(@PathVariable Integer id) {
        log.info("获取模型详情 - ID:{}", id);
        Model model = modelService.selectByPrimaryKey(id);
        if (model == null) {
            return JSONData.buildError("模型不存在");
        }
        return JSONData.buildSuccess(model);
    }

    /**
     * 创建模型
     */
    @PostMapping
    @Log("创建模型")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    public JSONData createModel(@RequestBody Model model) {
        log.info("创建模型 - 名称:{}", model.getModelName());
        
        // 设置创建者ID（需要从token中获取）
        // model.setCreatorId(getCurrentUserId());
        
        int result = modelService.insert(model);
        if (result > 0) {
            return JSONData.buildSuccess("模型创建成功");
        }
        return JSONData.buildError("模型创建失败");
    }

    /**
     * 更新模型
     */
    @PutMapping("/{id}")
    @Log("更新模型")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    public JSONData updateModel(@PathVariable Integer id, @RequestBody Model model) {
        log.info("更新模型 - ID:{}, 名称:{}", id, model.getModelName());
        
        model.setId(id);
        int result = modelService.updateByPrimaryKeySelective(model);
        if (result > 0) {
            return JSONData.buildSuccess("模型更新成功");
        }
        return JSONData.buildError("模型更新失败");
    }

    /**
     * 删除模型
     */
    @DeleteMapping("/{id}")
    @Log("删除模型")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    public JSONData deleteModel(@PathVariable Integer id) {
        log.info("删除模型 - ID:{}", id);
        
        int result = modelService.deleteByPrimaryKey(id);
        if (result > 0) {
            return JSONData.buildSuccess("模型删除成功");
        }
        return JSONData.buildError("模型删除失败");
    }

    /**
     * 批量删除模型
     */
    @PostMapping("/batch-delete")
    @Log("批量删除模型")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    public JSONData batchDelete(@RequestBody List<Integer> ids) {
        log.info("批量删除模型 - IDs:{}", ids);
        
        int count = 0;
        for (Integer id : ids) {
            count += modelService.deleteByPrimaryKey(id);
        }
        
        return JSONData.buildSuccess("成功删除 " + count + " 个模型");
    }

    /**
     * 分享/取消分享模型
     */
    @PutMapping("/{id}/share")
    @Log("分享模型")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    public JSONData shareModel(@PathVariable Integer id, @RequestParam Integer isPublic) {
        log.info("分享模型 - ID:{}, 公开:{}", id, isPublic);
        
        Model model = new Model();
        model.setId(id);
        model.setIsPublic(isPublic);
        
        int result = modelService.updateByPrimaryKeySelective(model);
        if (result > 0) {
            return JSONData.buildSuccess(isPublic == 1 ? "模型已公开" : "模型已设为私有");
        }
        return JSONData.buildError("操作失败");
    }

    /**
     * 启用/禁用模型
     */
    @PutMapping("/{id}/status")
    @Log("修改模型状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    public JSONData updateStatus(@PathVariable Integer id, @RequestParam String status) {
        log.info("修改模型状态 - ID:{}, 状态:{}", id, status);
        
        Model model = new Model();
        model.setId(id);
        model.setStatus(status);
        
        int result = modelService.updateByPrimaryKeySelective(model);
        if (result > 0) {
            return JSONData.buildSuccess("状态更新成功");
        }
        return JSONData.buildError("状态更新失败");
    }

    /**
     * 获取模型统计信息
     */
    @GetMapping("/statistics")
    @Log("获取模型统计")
    public JSONData getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总模型数
        List<Model> allModels = modelService.selectAll();
        stats.put("totalModels", allModels.size());
        
        // 公开模型数
        long publicModels = allModels.stream()
                .filter(m -> m.getIsPublic() != null && m.getIsPublic() == 1)
                .count();
        stats.put("publicModels", publicModels);
        
        // 活跃模型数
        long activeModels = allModels.stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()))
                .count();
        stats.put("activeModels", activeModels);
        
        // 模型类型统计
        Map<String, Long> typeStats = new HashMap<>();
        allModels.forEach(m -> {
            String type = m.getModelType();
            if (type != null) {
                typeStats.put(type, typeStats.getOrDefault(type, 0L) + 1);
            }
        });
        stats.put("typeStatistics", typeStats);
        
        return JSONData.buildSuccess(stats);
    }
}

