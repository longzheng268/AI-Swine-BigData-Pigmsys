package com.zhu.service.upload;

import com.zhu.mapper.UploadRecordMapper;
import com.zhu.pojo.EnvironmentData;
import com.zhu.pojo.UploadRecord;
import com.zhu.service.environment.EnvironmentDataService;
import com.zhu.utils.excel.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 数据上传 Service
 */
@Service
public class DataUploadService {

    @Autowired
    private UploadRecordMapper uploadRecordMapper;

    @Autowired
    private EnvironmentDataService environmentDataService;

    // 上传文件保存路径
    private static final String UPLOAD_PATH = "uploads/";

    /**
     * 上传环境监测数据
     */
    public UploadRecord uploadEnvironmentData(MultipartFile file, Integer userId, String username) {
        // 创建上传记录
        UploadRecord record = new UploadRecord();
        record.setOriginalName(file.getOriginalFilename());
        record.setFileSize(file.getSize());
        record.setFileType(getFileExtension(file.getOriginalFilename()));
        record.setDataType("ENVIRONMENT");
        record.setUploadUserId(userId);
        record.setUploadUsername(username);
        record.setStatus(UploadRecord.STATUS_PROCESSING);
        record.setCreateTime(new Date());

        try {
            // 保存文件
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = UPLOAD_PATH + fileName;
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            record.setFileName(fileName);
            record.setFilePath(filePath);

            // 解析 Excel
            List<Map<String, Object>> dataList = ExcelUtils.parseExcel(file);

            // 数据清洗
            ExcelUtils.fillMissingValues(dataList, "温度");
            ExcelUtils.fillMissingValues(dataList, "湿度");
            ExcelUtils.fillMissingValues(dataList, "CO₂浓度");
            ExcelUtils.fillMissingValues(dataList, "氨气浓度");
            ExcelUtils.fillMissingValues(dataList, "监测点", "未知监测点");

            // 转换为实体对象
            List<EnvironmentData> environmentDataList = new ArrayList<>();
            for (Map<String, Object> row : dataList) {
                try {
                    EnvironmentData data = mapToEnvironmentData(row);
                    environmentDataList.add(data);
                } catch (Exception e) {
                    // 单行数据转换失败，记录错误但继续处理
                    record.setFailedCount(record.getFailedCount() == null ? 1 : record.getFailedCount() + 1);
                }
            }

            // 批量插入
            if (!environmentDataList.isEmpty()) {
                environmentDataService.insertBatch(environmentDataList);
                record.setSuccessCount(environmentDataList.size());
            }

            // 更新状态
            record.setStatus(UploadRecord.STATUS_SUCCESS);

        } catch (Exception e) {
            record.setStatus(UploadRecord.STATUS_FAILED);
            record.setErrorMessage(e.getMessage());
        }

        // 保存上传记录
        uploadRecordMapper.insert(record);
        return record;
    }

    /**
     * 将 Map 转换为 EnvironmentData
     */
    private EnvironmentData mapToEnvironmentData(Map<String, Object> row) throws ParseException {
        EnvironmentData data = new EnvironmentData();
        
        data.setMonitorPoint(getStringValue(row, "监测点"));
        data.setMonitorLocation(getStringValue(row, "监测位置"));
        data.setLatitude(getBigDecimalValue(row, "纬度"));
        data.setLongitude(getBigDecimalValue(row, "经度"));
        data.setTemperature(getBigDecimalValue(row, "温度"));
        data.setHumidity(getBigDecimalValue(row, "湿度"));
        data.setCo2Concentration(getBigDecimalValue(row, "CO₂浓度"));
        data.setNh3Concentration(getBigDecimalValue(row, "氨气浓度"));
        data.setLightIntensity(getBigDecimalValue(row, "光照强度"));
        
        // 解析采集时间
        Object collectTimeObj = row.get("采集时间");
        if (collectTimeObj != null) {
            if (collectTimeObj instanceof Date) {
                data.setCollectTime((Date) collectTimeObj);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                data.setCollectTime(sdf.parse(collectTimeObj.toString()));
            }
        } else {
            data.setCollectTime(new Date());
        }
        
        data.setCreateTime(new Date());
        return data;
    }

    /**
     * 获取字符串值
     */
    private String getStringValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : value.toString().trim();
    }

    /**
     * 获取 BigDecimal 值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 查询上传记录
     */
    public List<UploadRecord> selectAll() {
        return uploadRecordMapper.selectAll();
    }

    /**
     * 按用户查询上传记录
     */
    public List<UploadRecord> selectByUserId(Integer userId) {
        return uploadRecordMapper.selectByUserId(userId);
    }
}



