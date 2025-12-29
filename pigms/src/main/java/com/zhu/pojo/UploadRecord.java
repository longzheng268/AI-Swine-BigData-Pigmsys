package com.zhu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据上传记录实体类
 */
@Data
public class UploadRecord implements Serializable {
    private Long id;
    private String fileName;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String dataType;
    private Integer uploadUserId;
    private String uploadUsername;
    private String status;
    private Integer successCount;
    private Integer failedCount;
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}



