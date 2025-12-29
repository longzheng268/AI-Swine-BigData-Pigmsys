package com.zhu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String userpassword;

    /**
     * 手机号码
     */
    private String usermobile;

    /**
     * 账号状态
     */
    private String state;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 其他信息
     */
    private String more;
}