package com.healthware.utils;

import com.healthware.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN_HEADER);
        if (token == null || !token.startsWith(Constants.TOKEN_PREFIX)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token已过期\"}");
            return false;
        }

        token = token.substring(Constants.TOKEN_PREFIX.length());

        try {
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token已过期，请重新登录\"}");
                return false;
            }

            Long userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            // 验证Redis中的Token是否存在
            String redisKey;
            if ("admin".equals(role)) {
                redisKey = Constants.ADMIN_TOKEN_PREFIX + userId;
            } else if ("doctor".equals(role)) {
                redisKey = Constants.DOCTOR_TOKEN_PREFIX + userId;
            } else {
                redisKey = Constants.USER_TOKEN_PREFIX + userId;
            }
            Object cachedToken = redisUtil.get(redisKey);
            if (cachedToken == null || !token.equals(cachedToken.toString())) {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token已失效，请重新登录\"}");
                return false;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效\"}");
            return false;
        }
    }
}
