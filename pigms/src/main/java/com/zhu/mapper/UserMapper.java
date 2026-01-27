//数据访问(Dao层)
package com.zhu.mapper;


import com.zhu.pojo.User;
import com.zhu.vo.QueryUserParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> queryAllUser();

    List<User> selectAllUserBCondition(QueryUserParam queryUserParam);

}



