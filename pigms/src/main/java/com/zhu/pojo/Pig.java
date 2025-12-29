package com.zhu.pojo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pig {
    private Integer id;

    /**
     * 猪的编号
     */
    private String pigisbn;

    /**
     * 猪的名称
     */
    private String pigname;

    /**
     * 年龄
     */
    private Integer pigage;

    /**
     * 性别
     */
    private Integer pinsex;

    /**
     * 出生日期
     */
    private Date manufacturedate;

    /**
     * 生成地
     */
    private String pigaddress;

    /**
     * 图片链接
     */
    private String pigimage;

    /**
     * 猪的种类
     */
    private Integer pigtype;
}