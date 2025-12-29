package com.zhu.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.pojo.OperationLog;
import com.zhu.service.log.OperationLogService;
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
 * 操作日志 Controller - 仅管理员可访问
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/log")
@PreAuthorize("hasRole('ADMIN')")
public class LogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 查询所有日志
     */
    @GetMapping("/list")
    public JSONData getAllLogs() {
        List<OperationLog> list = operationLogService.selectAll();
        return JSONData.buildSuccess(list);
    }

    /**
     * 分页查询日志
     */
    @PostMapping("/list/search/{page}/{size}")
    public JSONData searchLogs(
            @PathVariable Integer page,
            @PathVariable Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        
        PageInfo<OperationLog> pageInfo = operationLogService.selectByPage(
                page, size, username, operation, startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rows", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        return JSONData.buildSuccess(result);
    }

    /**
     * 删除日志
     */
    @DeleteMapping("/{id}")
    public JSONData deleteLog(@PathVariable Long id) {
        operationLogService.deleteByPrimaryKey(id);
        return JSONData.buildSuccess("删除成功");
    }

    /**
     * 清理过期日志（删除指定天数之前的日志）
     */
    @DeleteMapping("/cleanup/{days}")
    public JSONData cleanupLogs(@PathVariable Integer days) {
        Date beforeDate = new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L);
        int count = operationLogService.deleteByCreateTime(beforeDate);
        return JSONData.buildSuccess("已清理 " + count + " 条日志");
    }
}



