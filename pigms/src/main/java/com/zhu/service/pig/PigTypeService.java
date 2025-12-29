package com.zhu.service.pig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.zhu.mapper.PigTypeMapper;
import com.zhu.pojo.PigType;

import java.util.List;

@Service
public class PigTypeService{

    @Autowired
    private PigTypeMapper pigTypeMapper;

    
    public int deleteByPrimaryKey(Integer id) {
        return pigTypeMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(PigType record) {
        return pigTypeMapper.insert(record);
    }

    
    public int insertSelective(PigType record) {
        return pigTypeMapper.insertSelective(record);
    }

    
    public PigType selectByPrimaryKey(Integer id) {
        return pigTypeMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(PigType record) {
        return pigTypeMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateByPrimaryKey(PigType record) {
        return pigTypeMapper.updateByPrimaryKey(record);
    }

    public List<PigType> queryTypeSum(){
        return pigTypeMapper.queryTypeSum();
    }

}
