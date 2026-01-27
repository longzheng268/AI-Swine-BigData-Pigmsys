//定义用户的参数
package com.zhu.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryUserParam {
    private String name;

    private String address;

    private Integer userRole;

    private String sex;

}
