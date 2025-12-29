package com.zhu.controller;

import com.zhu.pojo.Login;
import com.zhu.service.login.LoginService;
import com.zhu.utils.json.ResultJson;
import com.zhu.utils.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * JWT 认证 Controller
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    /**
     * JWT 登录接口
     */
    @PostMapping("/login")
    public ResultJson login(@RequestParam String username, @RequestParam String password) {
        // 验证用户
        boolean isValid = loginService.checkLogin(username, password);
        
        if (!isValid) {
            return ResultJson.error().message("用户名或密码错误");
        }

        // 获取用户信息
        Login loginInfo = loginService.getLoginInfo(username);
        
        // 生成 JWT Token
        String token = jwtUtils.generateToken(
            username,
            loginInfo.getId(),
            loginInfo.getRoleId() != null ? loginInfo.getRoleId() : 2 // 默认普通用户
        );

        return ResultJson.ok()
                .data("token", token)
                .data("userId", loginInfo.getId())
                .data("username", loginInfo.getUsername())
                .data("roleId", loginInfo.getRoleId())
                .message("登录成功");
    }

    /**
     * 验证 Token
     */
    @GetMapping("/validate")
    public ResultJson validateToken(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResultJson.error().message("Token 格式错误");
        }

        String token = authorization.substring(7);
        
        if (jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            Integer userId = jwtUtils.getUserIdFromToken(token);
            Integer roleId = jwtUtils.getRoleIdFromToken(token);
            
            return ResultJson.ok()
                    .data("username", username)
                    .data("userId", userId)
                    .data("roleId", roleId)
                    .message("Token 有效");
        } else {
            return ResultJson.error().message("Token 无效或已过期");
        }
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public ResultJson refreshToken(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResultJson.error().message("Token 格式错误");
        }

        String oldToken = authorization.substring(7);
        String newToken = jwtUtils.refreshToken(oldToken);
        
        if (newToken != null) {
            return ResultJson.ok().data("token", newToken).message("Token 刷新成功");
        } else {
            return ResultJson.error().message("Token 刷新失败");
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResultJson logout() {
        // JWT 是无状态的，登出只需要前端删除 Token 即可
        return ResultJson.ok().message("登出成功");
    }
}



