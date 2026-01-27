//预测记录接口
package com.zhu.mapper;

import com.zhu.pojo.PredictionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface PredictionRecordMapper {
    
    int insert(PredictionRecord predictionRecord);
    
    int deleteByPrimaryKey(Long id);
    
    PredictionRecord selectByPrimaryKey(Long id);
    
    List<PredictionRecord> selectAll();
    
    List<PredictionRecord> selectByUserId(@Param("userId") Integer userId);
    
    List<PredictionRecord> selectByModelId(@Param("modelId") Integer modelId);
}



