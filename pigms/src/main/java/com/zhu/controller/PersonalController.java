package com.zhu.controller;

import com.zhu.pojo.Login;
import com.zhu.service.login.LoginService;
import com.zhu.utils.json.JSONData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonalController {

    @Autowired
    private LoginService loginService;


    @GetMapping("/personalInfo/{id}")
    public JSONData getPersonalInfo(@PathVariable("id") Integer id){
        Login personal = loginService.selectByPrimaryKey(id);
        System.out.println("personal:========>"+personal);
        return JSONData.buildSuccess(personal);
    }

}
