//猪(增删改查(分页查询))
package com.zhu.service.pig;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhu.pojo.User;
import com.zhu.vo.QueryPigParam;
import com.zhu.vo.QueryUserParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.zhu.pojo.Pig;
import com.zhu.mapper.PigMapper;

import java.util.List;

@Service
public class PigService{

    @Autowired
    private PigMapper pigMapper;

    
    public int deleteByPrimaryKey(Integer id) {
        return pigMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(Pig record) {
        return pigMapper.insert(record);
    }

    
    public int insertSelective(Pig record) {
        return pigMapper.insertSelective(record);
    }

    
    public Pig selectByPrimaryKey(Integer id) {
        return pigMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(Pig record) {
        return pigMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateByPrimaryKey(Pig record) {
        return pigMapper.updateByPrimaryKey(record);
    }


    //多个查询
    public List<Pig> selectAllPigBCondition(QueryPigParam queryPigParam){
        return pigMapper.selectAllPigBCondition(queryPigParam);
    }


    // 多个查询(分页）
    public PageInfo<Pig> selectAllPigByPageQuery(QueryPigParam queryPigParam, int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<Pig> list =selectAllPigBCondition(queryPigParam);
        PageInfo<Pig> result = new PageInfo<>(list);
        return result;

    }


}
