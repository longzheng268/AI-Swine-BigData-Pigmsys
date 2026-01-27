//预测模型接口
package com.zhu.mapper;

import com.zhu.pojo.Model;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ModelMapper {
    
    int insert(Model model);
    
    int deleteByPrimaryKey(Integer id);
    
    int updateByPrimaryKey(Model model);
    
    Model selectByPrimaryKey(Integer id);
    
    List<Model> selectAll();
    
    List<Model> selectByType(@Param("modelType") String modelType);
    
    List<Model> selectPublicModels();
    
    List<Model> selectByCreatorId(@Param("creatorId") Integer creatorId);
    
    List<Model> selectByCondition(@Param("modelName") String modelName, 
                                   @Param("modelType") String modelType, 
                                   @Param("status") String status);
    
    int updateByPrimaryKeySelective(Model model);
}



