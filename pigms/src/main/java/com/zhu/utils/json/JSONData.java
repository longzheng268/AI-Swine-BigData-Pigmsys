package com.zhu.utils.json;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: JsonData
 * @Description: TODO
 * @Author: zjw
 * @Date: 2020/09/08 09:04:49
 * @Version: V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JSONData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Integer code; // 状态码 0 表示成功，1表示处理中，-1表示失败
    private boolean flag;
    private Object data; // 数据
    private String message;// 描述



    // 成功，传入数据
    public static JSONData buildSuccess(String message) {
        return new JSONData(200,true,null,message);
    }

    // 成功，传入数据
    public static JSONData buildSuccess(Object data) {
        return new JSONData(200,true,data,null);
    }

    // 失败，传入描述信息
    public static JSONData buildError(String message) {
        return new JSONData(400, false,null,message);
    }

    // 失败，传入描述信息,状态码
    public static JSONData buildError(String message, Integer code) {
        return new JSONData(code, false,null,message);
    }

    // 成功，传入数据,及描述信息
    public static JSONData buildSuccess(Object data, String message) {
        return new JSONData(200,true, data, message);
    }

    // 成功，传入数据,及状态码
    public static JSONData buildSuccess(Object data, int code) {
        return new JSONData(code,true, data, null);
    }


}
