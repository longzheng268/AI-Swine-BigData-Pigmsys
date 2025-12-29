package com.zhu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhu.pojo.Login;
import com.zhu.service.login.LoginService;
import com.zhu.utils.json.JSONData;
import com.zhu.utils.json.ResultJson;
import com.zhu.utils.jwt.JwtUtils;
import com.zhu.utils.validation.CreateImageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin("*")
public class LoginController {
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/user/login")
    public ResultJson login(String username,String userpassword) throws JsonProcessingException {
        boolean flag = loginService.checkLogin(username, userpassword);

        if (flag) {
            // 获取用户信息
            Login loginInfo = loginService.getLoginInfo(username);
            
            // 生成 JWT Token
            String token = jwtUtils.generateToken(
                username,
                loginInfo.getId(),
                loginInfo.getRoleId() != null ? loginInfo.getRoleId() : 2 // 默认普通用户
            );
            
            return ResultJson.ok().data("token", token);
        }else {
            return ResultJson.error().message("用户名或密码错误");
        }
    }

    @GetMapping("/user/info/{token}")
    public ResultJson getUserInfo(@PathVariable("token") String token){
        try {
            // 从 JWT Token 中获取用户名
            String username = jwtUtils.getUsernameFromToken(token);
            Integer userId = jwtUtils.getUserIdFromToken(token);
            Integer roleId = jwtUtils.getRoleIdFromToken(token);
            
            // 获取完整用户信息
            Login loginInfo = loginService.getLoginInfo(username);
            
            if (loginInfo != null) {
                return ResultJson.ok()
                    .data("id", loginInfo.getId())
                    .data("name", loginInfo.getUsername())
                    .data("roleId", loginInfo.getRoleId() != null ? loginInfo.getRoleId() : roleId);
            } else {
                return ResultJson.error().message("用户不存在");
            }
        } catch (Exception e) {
            // 如果 Token 无效，尝试使用旧方式（兼容旧数据）
            Login loginInfo = loginService.getLoginInfo(token);
            if (loginInfo != null) {
                return ResultJson.ok()
                    .data("id", loginInfo.getId())
                    .data("name", loginInfo.getUsername())
                    .data("roleId", loginInfo.getRoleId());
            } else {
                return ResultJson.error().message("Token 无效");
            }
        }
    }

    @GetMapping("/user/logout/{token}")
    public ResultJson logout(@PathVariable("token") Object token){
        return ResultJson.ok();
    }

    @PostMapping("/user/pwd")
    public ResultJson checkPassword(Integer userId,String oldPassword){
        System.out.println(userId+"===="+oldPassword);
        Login login = loginService.selectByPrimaryKey(userId);
        if (oldPassword.equals(login.getUserpassword())) {
            return ResultJson.ok();
        }else {
            return ResultJson.error();
        }
    }

    @PutMapping("/user/pwd")
    public ResultJson updatePassword(Integer userId,String newPassword){
        System.out.println(userId+"====>"+newPassword);
        Login login = new Login();
        login.setId(userId);
        login.setUserpassword(newPassword);
        loginService.updateByPrimaryKeySelective(login);
        return ResultJson.ok();
    }

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    public ResultJson register(@RequestBody Login login) {
        try {
            // 检查用户名是否已存在
            Login existingUser = loginService.getLoginInfo(login.getUsername());
            if (existingUser != null) {
                return ResultJson.error().message("用户名已存在");
            }
            
            // 插入新用户
            loginService.insert(login);
            return ResultJson.ok().message("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultJson.error().message("注册失败：" + e.getMessage());
        }
    }

    /**
     * 生成验证码
     */
    @GetMapping("/getVerify")
//    @RequestMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            CreateImageCode randomValidateCode = new CreateImageCode();
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error("获取验证码失败>>>>   ", e);
        }
    }

    /**
     * 校验验证码
     */
    @PostMapping("/checkVerify")
    public JSONData checkVerify(@RequestParam String code, HttpSession session) {
        try{
            System.out.println(code+"---------");
            //从session中获取随机数
            String inputStr = code;
            System.out.println(session.getId());
            String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
            System.out.println(random+"-----------");
            if (random == null) {
                return JSONData.buildError("错误");
            }
            if (random.equals(inputStr)) {
                return JSONData.buildSuccess("成功");
            } else {
                return JSONData.buildError("验证码错误");
            }
        }catch (Exception e){
            e.printStackTrace();
//            logger.error("验证码校验失败", e);
            return JSONData.buildError("验证码校验失败");
        }
    }



}
