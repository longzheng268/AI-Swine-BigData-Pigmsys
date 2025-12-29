package com.zhu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 环境监测数据实体类
 */
@Data
public class EnvironmentData implements Serializable {
    private Long id;
    private String monitorPoint;
    private String monitorLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal co2Concentration;
    private BigDecimal nh3Concentration;
    private BigDecimal lightIntensity;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectTime;
    
    private Integer isAbnormal;
    private String abnormalReason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}



