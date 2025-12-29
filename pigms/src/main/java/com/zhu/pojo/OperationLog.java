package com.zhu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志实体类
 */
@Data
public class OperationLog implements Serializable {
    private Long id;
    private Integer userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ip;
    private Long executeTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}



