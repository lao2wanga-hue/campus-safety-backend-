package com.campus.safety.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // ⭐ 允许所有来源（生产环境可改为具体域名）
        config.addAllowedOriginPattern("*");
        
        // ⭐ 允许所有头
        config.addAllowedHeader("*");
        
        // ⭐ 允许所有方法
        config.addAllowedMethod("*");
        
        // ⭐ 允许携带凭证
        config.setAllowCredentials(true);
        
        // ⭐ 预检请求缓存时间
        config.setMaxAge(3600L);
        
        // ⭐ 暴露必要的响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}