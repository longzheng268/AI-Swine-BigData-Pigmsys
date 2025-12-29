package com.zhu.config;

import com.zhu.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置
 * 配置为宽松模式，不影响现有接口
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 允许所有来源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许的方法
        configuration.setAllowedHeaders(Arrays.asList("*")); // 允许所有请求头
        configuration.setAllowCredentials(true); // 允许携带凭证
        configuration.setMaxAge(3600L); // 预检请求的有效期
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 对所有路径生效
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 关闭 CSRF
            .csrf().disable()
            // 配置 CORS
            .cors().configurationSource(corsConfigurationSource())
            .and()
            // 不使用 Session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置权限控制
            .authorizeRequests()
                // 公开接口（不需要登录）
                .antMatchers("/user/login", "/auth/login", "/getVerify", "/checkVerify").permitAll()
                // 用户注册接口（公开）
                .antMatchers("/user/register").permitAll()
                // 用户信息接口（登录后获取用户信息）
                .antMatchers("/user/info/**").permitAll()
                // 登出接口（允许所有人访问）
                .antMatchers("/user/logout/**", "/auth/logout").permitAll()
                // 允许 /user 路径（虽然后端没有此端点，但避免 403 错误，会返回 404）
                .antMatchers("/user").permitAll()
                // 允许 /log 路径（虽然后端没有此根端点，但避免 403 错误，会返回 404）
                .antMatchers("/log").permitAll()
                // 公共数据展示（不需要登录，但功能受限）
                .antMatchers("/pig/getTypeSum", "/pigInfo/list").permitAll()
                // 个人信息、生猪信息查询（登录后可访问）
                .antMatchers("/personalInfo/**", "/pigInfo/**").authenticated()
                // 环境监测数据（登录后可访问，具体权限由 @PreAuthorize 控制）
                .antMatchers("/environment/**").authenticated()
                // 预测分析、数据上传（由 @PreAuthorize 控制，需要管理员或科研人员）
                .antMatchers("/prediction/**", "/upload/**").authenticated()
                // 操作日志（由 @PreAuthorize 控制，仅管理员）
                .antMatchers("/log/**").authenticated()
                // 用户管理（由 @PreAuthorize 控制，仅管理员）
                .antMatchers("/userinfo/**").authenticated()
                // 大屏数据接口（登录后可访问）
                .antMatchers("/api/dashboard/**").authenticated()
                // Hadoop接口（登录后可访问）
                .antMatchers("/api/hadoop/**").authenticated()
                // 其他接口也需要认证
                .anyRequest().authenticated()
            .and()
            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}


