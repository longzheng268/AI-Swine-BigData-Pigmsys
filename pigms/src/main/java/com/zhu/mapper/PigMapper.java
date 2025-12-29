package com.zhu.mapper;

import com.zhu.pojo.Pig;
import com.zhu.pojo.User;
import com.zhu.vo.QueryPigParam;
import com.zhu.vo.QueryUserParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Pig record);

    int insertSelective(Pig record);

    Pig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Pig record);

    int updateByPrimaryKey(Pig record);

    List<Pig> queryAllPig();

    List<Pig> selectAllPigBCondition(QueryPigParam queryPigParam);


}