package com.zhu.service.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhu.vo.QueryUserParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.zhu.mapper.UserMapper;
import com.zhu.pojo.User;

import java.util.List;

@Service
public class UserService{

    @Autowired
    private UserMapper userMapper;

    
    public int deleteByPrimaryKey(Integer id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(User record) {
        return userMapper.insert(record);
    }

    
    public int insertSelective(User record) {
        return userMapper.insertSelective(record);
    }

    
    public User selectByPrimaryKey(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateByPrimaryKey(User record) {
        return userMapper.updateByPrimaryKey(record);
    }

    public List<User> queryAllUser(){return userMapper.queryAllUser();}

    //多个查询
    public List<User> selectAllUserBCondition(QueryUserParam queryUserParam){
        return userMapper.selectAllUserBCondition(queryUserParam);
    }


    // 多个查询(分页）
    public PageInfo<User> selectAllUserByPageQuery(QueryUserParam queryUserParam, int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<User> list =selectAllUserBCondition(queryUserParam);
        PageInfo<User> result = new PageInfo<>(list);
        return result;

    }



}
