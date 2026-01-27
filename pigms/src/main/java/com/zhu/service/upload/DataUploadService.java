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
import java.io.IOException;
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
    private static String UPLOAD_PATH = System.getProperty("user.dir") + "/uploads/";    public UploadRecord uploadEnvironmentData(MultipartFile file, Integer userId, String username) {
        // 创建上传记录
        UploadRecord record = new UploadRecord();
        record.setOriginalName(file.getOriginalFilename());
        record.setFileSize(file.getSize());
        record.setFileType(getFileExtension(file.getOriginalFilename()));
        record.setDataType("ENVIRONMENT");
        record.setUploadUserId(userId);
        record.setUploadUsername(username);
        record.setStatus("PROCESSING");
        record.setCreateTime(new Date());

        try {
            List<Map<String, Object>> dataList = ExcelUtils.parseExcel(file);  // 使用原始的 MultipartFile
            // ==== 1. 生成安全的文件名 ====
            String originalFilename = file.getOriginalFilename();
            String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID().toString() + "_" + safeFilename;

            // ==== 2. 构建完整的文件路径 ====
            // 确保UPLOAD_PATH以斜杠结尾
            if (!UPLOAD_PATH.endsWith("/") && !UPLOAD_PATH.endsWith("\\")) {
                UPLOAD_PATH += "/";
            }

            String filePath = UPLOAD_PATH + fileName;

            // ==== 3. 检查并创建目录（关键步骤）====
            File dest = new File(filePath);
            File parentDir = dest.getParentFile();

            // 如果父目录不存在，创建它
            if (parentDir != null && !parentDir.exists()) {
                // 使用mkdirs()而不是mkdir()，创建多级目录
                boolean dirsCreated = parentDir.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
                }
            }

            // 检查目录是否可写
            if (!parentDir.canWrite()) {
                throw new IOException("目录不可写，请检查权限: " + parentDir.getAbsolutePath());
            }

            // ==== 4. 检查文件是否已存在 ====
            if (dest.exists()) {
                // 如果文件已存在，先删除（或修改文件名）
                boolean deleted = dest.delete();
                if (!deleted) {
                    throw new IOException("文件已存在且无法删除: " + filePath);
                }
            }

            // ==== 5. 保存文件 ====
            // 方法1：使用transferTo（推荐）
            try {

// 在 transferTo 之前确保目录存在
                File destDir = dest.getParentFile();
                if (destDir != null && !destDir.exists()) {
                    boolean created = destDir.mkdirs();
                    if (!created) {
                        throw new IOException("无法创建上传目录: " + destDir.getAbsolutePath());
                    }
                }
                // 然后再执行文件转移
                file.transferTo(dest);
            } catch (IOException e) {
                throw new IOException("无法创建上传目录或保存文件: " + e.getMessage());
            }

            // 或者方法2：使用流复制（如果transferTo失败）
        /*
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        */

            // ==== 6. 验证文件是否保存成功 ====
            if (!dest.exists()) {
                throw new IOException("文件保存失败: 目标文件不存在");
            }

            if (dest.length() == 0) {
                dest.delete(); // 删除空文件
                throw new IOException("文件保存失败: 文件内容为空");
            }

            // ==== 7. 设置文件信息 ====
            record.setFileName(fileName);
            record.setFilePath(dest.getAbsolutePath()); // 使用绝对路径

            // ==== 8. 解析 Excel（从保存的文件读取）====

            // ==== 后续处理代码（保持不变）====
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
                    record.setFailedCount(record.getFailedCount() == null ? 1 : record.getFailedCount() + 1);
                }
            }

            // 批量插入
            if (!environmentDataList.isEmpty()) {
                environmentDataService.insertBatch(environmentDataList);
                record.setSuccessCount(environmentDataList.size());
            }

            // 更新状态
            record.setStatus("SUCCESS");

        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage(e.getMessage() + " [文件路径: " + UPLOAD_PATH + "]");
        }

        // 保存上传记录
        uploadRecordMapper.insert(record);
        return record;
    }


    /**
     * 上传环境监测数据
     */
    /*public UploadRecord uploadEnvironmentData(MultipartFile file, Integer userId, String username) {
        // 创建上传记录
        UploadRecord record = new UploadRecord();
        record.setOriginalName(file.getOriginalFilename());
        record.setFileSize(file.getSize());
        record.setFileType(getFileExtension(file.getOriginalFilename()));
        record.setDataType("ENVIRONMENT");
        record.setUploadUserId(userId);
        record.setUploadUsername(username);
        record.setStatus("PROCESSING");
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
            record.setStatus("SUCCESS");

        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage(e.getMessage());
        }

        // 保存上传记录
        uploadRecordMapper.insert(record);
        return record;
    }*/

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



