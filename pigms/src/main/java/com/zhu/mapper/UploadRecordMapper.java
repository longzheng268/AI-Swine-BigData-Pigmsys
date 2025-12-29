package com.zhu.mapper;

import com.zhu.pojo.UploadRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据上传记录 Mapper 接口
 */
@Mapper
public interface UploadRecordMapper {
    
    int insert(UploadRecord uploadRecord);
    
    int updateByPrimaryKey(UploadRecord uploadRecord);
    
    UploadRecord selectByPrimaryKey(Long id);
    
    List<UploadRecord> selectAll();
    
    List<UploadRecord> selectByUserId(@Param("userId") Integer userId);
}



