package com.zhu.mapper;

import com.zhu.pojo.EnvironmentStandard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 环境标准 Mapper 接口
 */
@Mapper
public interface EnvironmentStandardMapper {
    
    EnvironmentStandard selectByPrimaryKey(Integer id);
    
    EnvironmentStandard selectByParameterName(@Param("parameterName") String parameterName);
    
    List<EnvironmentStandard> selectAll();
}



