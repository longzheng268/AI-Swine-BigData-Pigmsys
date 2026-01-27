//预测模型实体类
package com.zhu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class Model implements Serializable {
    private Integer id;
    private String modelName;
    private String modelType;
    private String modelPath;
    private BigDecimal accuracy;
    private String description;
    private String inputFeatures;
    private String outputFeatures;
    private Integer creatorId;
    private Integer isPublic;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}



