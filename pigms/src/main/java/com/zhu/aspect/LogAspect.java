package com.zhu.aspect;

import com.alibaba.fastjson.JSON;
import com.zhu.annotation.Log;
import com.zhu.pojo.OperationLog;
import com.zhu.service.log.OperationLogService;
import com.zhu.utils.jwt.JwtUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志 AOP 切面
 */
@Aspect
@Component
public class LogAspect {

    @Autowired(required = false)
    private OperationLogService operationLogService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 定义切点：所有带 @Log 注解的方法
     */
    @Pointcut("@annotation(com.zhu.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 执行目标方法
        Object result = joinPoint.proceed();
        
        // 计算执行时间
        long executeTime = System.currentTimeMillis() - startTime;
        
        // 保存日志
        try {
            saveLog(joinPoint, executeTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 保存操作日志
     */
    private void saveLog(ProceedingJoinPoint joinPoint, long executeTime) {
        if (operationLogService == null) {
            return;
        }

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        // 创建日志对象
        OperationLog log = new OperationLog();
        log.setOperation(logAnnotation.value());
        log.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
        log.setExecuteTime(executeTime);
        log.setIp(getIpAddress(request));
        log.setCreateTime(new Date());

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            try {
                log.setParams(JSON.toJSONString(args));
            } catch (Exception e) {
                log.setParams("参数解析失败");
            }
        }

        // 获取用户信息
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                if (jwtUtils.validateToken(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);
                    Integer userId = jwtUtils.getUserIdFromToken(token);
                    log.setUsername(username);
                    log.setUserId(userId);
                }
            } catch (Exception e) {
                // Token 解析失败，不记录用户信息
            }
        }

        // 保存日志
        operationLogService.insert(log);
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}



