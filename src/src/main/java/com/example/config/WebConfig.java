package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @author wzs
 * @date 2025年04月30日 23:09
 */
@Configuration
public class WebConfig {

/**
 * 配置跨域资源共享(CORS)的Bean，用于解决前端和后端之间的跨域问题
 *
 * @return CorsConfigurationSource 用于提供跨域配置的源
 */
@Bean
public CorsConfigurationSource corsConfigurationSource(){
    // 创建一个新的CORS配置对象
    CorsConfiguration config = new CorsConfiguration();
    // 允许所有域名进行跨域调用
    config.addAllowedOriginPattern("*");
    // 允许所有头进行跨域调用
    config.addAllowedHeader("*");
    // 允许所有方法进行跨域调用
    config.addAllowedMethod("*");
    // 设置是否支持用户凭证
    config.setAllowCredentials(true);

    // 创建一个新的基于URL的CORS配置源
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 将CORS配置注册到所有路径
    source.registerCorsConfiguration("/**", config);
    // 返回CORS配置源
    return source;
}

}
