//与登录相关的逻辑
package com.zhu.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.zhu.mapper.LoginMapper;
import com.zhu.pojo.Login;
@Service
public class LoginService{

    @Autowired
    private LoginMapper loginMapper;

    
    public int deleteByPrimaryKey(Integer id) {
        return loginMapper.deleteByPrimaryKey(id);
    }

    
    public int insert(Login record) {
        return loginMapper.insert(record);
    }

    
    public int insertSelective(Login record) {
        return loginMapper.insertSelective(record);
    }

    
    public Login selectByPrimaryKey(Integer id) {
        return loginMapper.selectByPrimaryKey(id);
    }

    
    public int updateByPrimaryKeySelective(Login record) {
        return loginMapper.updateByPrimaryKeySelective(record);
    }

    
    public int updateByPrimaryKey(Login record) {
        return loginMapper.updateByPrimaryKey(record);
    }

    public boolean checkLogin(String username,String password){
        int row = loginMapper.login(username, password);
        if (row > 0) {
            return true;
        }else {
            return false;
        }
    }

    public Login getLoginInfo(String username){
        return loginMapper.loginInfo(username);
    }

}
