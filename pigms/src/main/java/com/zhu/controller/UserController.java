package com.zhu.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.pojo.User;
import com.zhu.service.user.UserService;

import com.zhu.utils.json.JSONData;
import com.zhu.utils.json.ResultJson;
import com.zhu.vo.QueryUserParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 查询用户列表 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("userinfo/list/search/{page}/{size}")
    public ResultJson query(@RequestBody QueryUserParam queryUserParam,
                            @PathVariable("page") Integer currentPage,
                            @PathVariable("size") Integer pageSize){
        System.out.println(currentPage);
        PageInfo<User> userPageInfo = userService.selectAllUserByPageQuery(queryUserParam, currentPage, pageSize);
        return ResultJson.ok().data("rows",userPageInfo.getList()).data("total",userPageInfo.getTotal());
    }

    /**
     * 查询用户详情 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/userinfo/{id}")
    public JSONData getUserById(@PathVariable("id")Integer id){
        User user = userService.selectByPrimaryKey(id);
        return JSONData.buildSuccess(user);
    }

    /**
     * 更新用户信息 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/userinfo/{id}")
    public ResultJson updateUser(@PathVariable("id")Integer id,@RequestBody User user){
        System.out.println(id);
        System.out.println(user);
        userService.updateByPrimaryKey(user);
        return ResultJson.ok();
    }

    /**
     * 添加用户 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addUser")
    public ResultJson addUser(@RequestBody User user){
        System.out.println(user);
        userService.insert(user);
        return ResultJson.ok();
    }

    /**
     * 删除用户 - 仅管理员
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/userinfo/{id}")
    public ResultJson deleteUser(@PathVariable("id") Integer id){
        userService.deleteByPrimaryKey(id);
        return ResultJson.ok();
    }




}
