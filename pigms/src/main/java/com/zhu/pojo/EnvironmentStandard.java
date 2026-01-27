//环境标准实体
package com.zhu.pojo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class EnvironmentStandard implements Serializable {
    private Integer id;
    private String parameterName;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String unit;
    private String description;
}



