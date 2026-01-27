//表示用户对象
package com.zhu.pojo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    /**
     * 用户信息号
     */
    private String userisbn;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private String sex;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 角色编号
     */
    private Integer userrole;
}