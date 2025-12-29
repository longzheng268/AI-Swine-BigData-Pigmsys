package com.zhu.mapper;

import com.zhu.pojo.EnvironmentData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 环境监测数据 Mapper 接口
 */
@Mapper
public interface EnvironmentDataMapper {
    
    int insert(EnvironmentData environmentData);
    
    int insertBatch(List<EnvironmentData> list);
    
    int deleteByPrimaryKey(Long id);
    
    int updateByPrimaryKey(EnvironmentData environmentData);
    
    EnvironmentData selectByPrimaryKey(Long id);
    
    List<EnvironmentData> selectAll();
    
    List<EnvironmentData> selectByCondition(@Param("monitorPoint") String monitorPoint,
                                            @Param("startTime") Date startTime,
                                            @Param("endTime") Date endTime,
                                            @Param("isAbnormal") Integer isAbnormal);
    
    List<EnvironmentData> selectLatestByMonitorPoint(@Param("limit") Integer limit);
    
    int countAbnormal(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}



