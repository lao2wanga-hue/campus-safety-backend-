package com.campus.safety.interceptor;

import com.campus.safety.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        // 登录接口不需要 token
        if ("/api/user/login".equals(request.getRequestURI()) ||
            "/api/user/register".equals(request.getRequestURI())) {
            return true;
        }
        
        // 文档接口不需要 token
        if ("/doc.html".equals(request.getRequestURI()) || 
            request.getRequestURI().startsWith("/v3/") || 
            request.getRequestURI().startsWith("/swagger-ui")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        
        token = token.substring(7);
        if (jwtUtil.isTokenExpired(token)) {
            response.setStatus(401);
            return false;
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        
        return true;
    }
}