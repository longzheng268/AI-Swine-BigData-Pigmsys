package com.zhu.utils.json;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResultJson {

    private Boolean flag;

    private Integer code;

    private String message;

    private Map<String, Object> data = new HashMap<>();

    // 把构造方法私有化
    private ResultJson() {}

    // 成功静态方法
    public static ResultJson ok() {
        ResultJson r = new ResultJson();
        r.setFlag(true);
        r.setCode(200);
        r.setMessage("成功");
        return r;
    }

    // 失败静态方法
    public static ResultJson error() {
        ResultJson r = new ResultJson();
        r.setFlag(false);
        r.setCode(201);
        r.setMessage("失败");
        return r;
    }

    public ResultJson success(Boolean success){
        this.setFlag(success);
        return this;
    }

    public ResultJson message(String message){
        this.setMessage(message);
        return this;
    }

    public ResultJson code(Integer code){
        this.setCode(code);
        return this;
    }

    public ResultJson data(String key, Object value){
        this.data.put(key, value);
        return this;
    }
}

