package com.zhu.pojo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色实体类
 */
@Data
public class Role implements Serializable {
    private Integer id;
    private String roleName;
    private String roleCode;
    private String description;
    private Date createTime;
}



