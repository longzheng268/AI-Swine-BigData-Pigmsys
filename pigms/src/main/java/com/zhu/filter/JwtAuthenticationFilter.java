//JWT认证过滤器
package com.zhu.filter;

import com.zhu.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("JWT过滤器处理请求: {}", requestURI);
        
        // 从请求头中获取 Token
        String token = request.getHeader("Authorization");
        log.info("Authorization头: {}", token != null ? "存在" : "不存在");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            // 验证 Token
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                Integer roleId = jwtUtils.getRoleIdFromToken(token);
                
                log.info("Token验证成功 - 用户: {}, 角色ID: {}", username, roleId);
                
                // 设置角色权限
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + getRoleCode(roleId)));
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                // 设置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("认证对象已设置到SecurityContext");
            } else {
                log.warn("Token验证失败");
            }
        } else {
            log.warn("请求未携带有效的Authorization头");
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 根据角色ID获取角色代码
     */
    private String getRoleCode(Integer roleId) {
        if (roleId == null) {
            return "USER";
        }
        switch (roleId) {
            case 1:
                return "ADMIN";
            case 2:
                return "USER";
            case 3:
                return "RESEARCHER";
            default:
                return "USER";
        }
    }
}



