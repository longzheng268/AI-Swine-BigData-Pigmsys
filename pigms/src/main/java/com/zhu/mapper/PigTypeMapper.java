package com.zhu.mapper;

import com.zhu.pojo.PigType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PigTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PigType record);

    int insertSelective(PigType record);

    PigType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PigType record);

    int updateByPrimaryKey(PigType record);


    List<PigType> queryTypeSum();


}