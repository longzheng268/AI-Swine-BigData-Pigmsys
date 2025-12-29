package com.zhu.mapper;

import com.zhu.pojo.Login;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface LoginMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Login record);

    int insertSelective(Login record);

    Login selectByPrimaryKey(Integer id);


    int updateByPrimaryKeySelective(Login record);

    int updateByPrimaryKey(Login record);

    int login(@Param("param1") String username, @Param("param2") String userpassword);

    Login loginInfo(String username);
}