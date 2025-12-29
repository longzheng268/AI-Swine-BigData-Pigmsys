package com.zhu.mapper;

import com.zhu.pojo.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 操作日志 Mapper 接口
 */
@Mapper
public interface OperationLogMapper {
    
    int insert(OperationLog operationLog);
    
    int deleteByPrimaryKey(Long id);
    
    List<OperationLog> selectAll();
    
    List<OperationLog> selectByCondition(@Param("username") String username,
                                         @Param("operation") String operation,
                                         @Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime);
    
    int deleteByCreateTime(@Param("beforeDate") Date beforeDate);
}



