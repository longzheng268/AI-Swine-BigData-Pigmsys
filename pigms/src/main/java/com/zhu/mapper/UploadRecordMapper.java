//数据上传记录接口
package com.zhu.mapper;

import com.zhu.pojo.UploadRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface UploadRecordMapper {
    
    int insert(UploadRecord uploadRecord);
    
    int updateByPrimaryKey(UploadRecord uploadRecord);
    
    UploadRecord selectByPrimaryKey(Long id);
    
    List<UploadRecord> selectAll();
    
    List<UploadRecord> selectByUserId(@Param("userId") Integer userId);
}



