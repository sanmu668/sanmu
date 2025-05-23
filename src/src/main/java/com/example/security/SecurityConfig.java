package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author wzs
 * @date 2025年04月28日 1:16
 */
/**
 * 配置Spring Security的类
 * 用于定义Web安全配置，比如HTTP请求的安全约束和CSRF保护
 */
@Configuration
public class SecurityConfig {
    /**
     * 创建并配置SecurityFilterChain bean
     * 该方法主要用于配置HTTP安全设置，比如请求授权规则和CSRF保护
     *
     * @param http HttpSecurity实例，用于配置Web安全
     * @return 配置好的SecurityFilterChain实例
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 禁用CSRF保护，通常在REST API中不需要CSRF保护
        http.csrf(csrf -> csrf.disable())
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许所有以"api/auth/**"开头的请求无需认证即可访问
                        .requestMatchers("/api/auth/**","/chat/**").permitAll()
                        // 其他所有请求都需要经过认证
                        .anyRequest().authenticated());
        // 构建并返回配置好的SecurityFilterChain
        return http.build();
    }
}
