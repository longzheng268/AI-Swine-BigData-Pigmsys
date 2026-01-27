//预测记录实体类
package com.zhu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//预测记录实体类
@Data
public class PredictionRecord implements Serializable {
    private Long id;
    private Integer modelId;
    private String modelName;
    private String inputData;
    private String outputData;
    private BigDecimal accuracy;
    private Long executeTime;
    private Integer userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}



