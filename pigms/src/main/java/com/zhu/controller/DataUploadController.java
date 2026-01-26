package com.zhu.controller;

import com.zhu.annotation.Log;
import com.zhu.pojo.UploadRecord;
import com.zhu.service.upload.DataUploadService;
import com.zhu.utils.json.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 数据上传 Controller
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/upload")
public class DataUploadController {

    @Autowired
    private DataUploadService dataUploadService;

    /**
     * 上传环境监测数据（Excel）- 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @Log("上传环境监测数据")
    @PostMapping("/environment")
    public JSONData uploadEnvironmentData(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "1") Integer userId,
            @RequestParam(required = false, defaultValue = "guest") String username) {
        
        if (file.isEmpty()) {
            return JSONData.buildError("文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return JSONData.buildError("只支持 Excel 文件（.xlsx 或 .xls）");
        }

        try {
            UploadRecord record = dataUploadService.uploadEnvironmentData(file, userId, username);
            // 检查上传记录状态，如果后端处理失败则返回错误响应（修复前端显示成功但记录显示失败的问题）
            if (UploadRecord.STATUS_FAILED.equals(record.getStatus())) {
                String errorMsg = record.getErrorMessage() != null ? record.getErrorMessage() : "数据导入失败";
                return JSONData.buildError("上传失败：" + errorMsg);
            }
            return JSONData.buildSuccess(record);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONData.buildError("上传失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有上传记录 - 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @GetMapping("/records")
    public JSONData getAllUploadRecords() {
        List<UploadRecord> list = dataUploadService.selectAll();
        return JSONData.buildSuccess(list);
    }

    /**
     * 按用户查询上传记录 - 仅管理员和科研人员
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER')")
    @GetMapping("/records/{userId}")
    public JSONData getUploadRecordsByUser(@PathVariable Integer userId) {
        List<UploadRecord> list = dataUploadService.selectByUserId(userId);
        return JSONData.buildSuccess(list);
    }
}



