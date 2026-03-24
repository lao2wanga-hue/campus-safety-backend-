package com.campus.safety.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ⭐ 禁用 CSRF（API 不需要）
            .csrf(csrf -> csrf.disable())
            
            // ⭐ 禁用表单登录（使用 JWT）
            .formLogin(form -> form.disable())
            
            // ⭐ 禁用 HTTP Basic 认证
            .httpBasic(basic -> basic.disable())
            
            // ⭐ 禁用会话管理（使用 JWT 无状态）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // ⭐ 所有请求都允许（JWT 拦截器处理认证）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().permitAll()
            )
            
            // ⭐ 禁用登出
            .logout(logout -> logout.disable());
        
        return http.build();
    }
}