package com.zhu.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.annotation.Log;
import com.zhu.pojo.EnvironmentData;
import com.zhu.service.environment.EnvironmentDataService;
import com.zhu.utils.json.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 环境监测数据 Controller
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/environment")
public class EnvironmentController {

    @Autowired
    private EnvironmentDataService environmentDataService;

    /**
     * 添加环境监测数据 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Log("添加环境监测数据")
    @PostMapping("/add")
    public JSONData addEnvironmentData(@RequestBody EnvironmentData environmentData) {
        environmentDataService.insert(environmentData);
        return JSONData.buildSuccess("添加成功");
    }

    /**
     * 删除环境监测数据 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Log("删除环境监测数据")
    @DeleteMapping("/{id}")
    public JSONData deleteEnvironmentData(@PathVariable Long id) {
        environmentDataService.deleteByPrimaryKey(id);
        return JSONData.buildSuccess("删除成功");
    }

    /**
     * 更新环境监测数据 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Log("更新环境监测数据")
    @PutMapping("/{id}")
    public JSONData updateEnvironmentData(@PathVariable Long id, @RequestBody EnvironmentData environmentData) {
        environmentData.setId(id);
        environmentDataService.updateByPrimaryKey(environmentData);
        return JSONData.buildSuccess("更新成功");
    }

    /**
     * 查询单条数据
     */
    @GetMapping("/{id}")
    public JSONData getEnvironmentData(@PathVariable Long id) {
        EnvironmentData data = environmentDataService.selectByPrimaryKey(id);
        return JSONData.buildSuccess(data);
    }

    /**
     * 查询所有数据
     */
    @GetMapping("/list")
    public JSONData getAllEnvironmentData() {
        List<EnvironmentData> list = environmentDataService.selectAll();
        return JSONData.buildSuccess(list);
    }

    /**
     * 分页查询
     */
    @PostMapping("/list/search/{page}/{size}")
    public JSONData searchEnvironmentData(
            @PathVariable Integer page,
            @PathVariable Integer size,
            @RequestParam(required = false) String monitorPoint,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(required = false) Integer isAbnormal) {
        
        PageInfo<EnvironmentData> pageInfo = environmentDataService.selectByPage(
                page, size, monitorPoint, startTime, endTime, isAbnormal);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return JSONData.buildSuccess(result);
    }

    /**
     * 获取最新的监测数据（用于实时展示）
     */
    @GetMapping("/latest")
    public JSONData getLatestData(@RequestParam(defaultValue = "10") Integer limit) {
        List<EnvironmentData> list = environmentDataService.selectLatestByMonitorPoint(limit);
        return JSONData.buildSuccess(list);
    }

    /**
     * 统计异常数据
     */
    @GetMapping("/abnormal/count")
    public JSONData countAbnormal(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        int count = environmentDataService.countAbnormal(startTime, endTime);
        return JSONData.buildSuccess(count);
    }
}



