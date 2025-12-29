package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPigParam {
    private String pigISBN;
    private String pigName;
    private Integer pigType;
    private Integer pinSex;
}
