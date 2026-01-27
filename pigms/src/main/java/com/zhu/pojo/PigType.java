//猪的种类
package com.zhu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PigType {
    private Integer id;

    /**
    * 猪的种类
    */
    private String pigType;

    /**
    * 猪的种类的数量
    */
    private Integer pigTypeSum;
}