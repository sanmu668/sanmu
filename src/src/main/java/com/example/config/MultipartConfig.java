package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * @author wzs
 * @date 2025年05月07日 14:01
 */
@Configuration
public class MultipartConfig {
    /**
     * 配置并返回一个MultipartResolver bean，用于处理多部分请求
     * 主要用于文件上传等场景
     *
     * @return MultipartResolver 实例，用于解析多部分请求
     */
    @Bean
    public MultipartResolver multipartResolver() {
        // 创建一个StandardServletMultipartResolver实例
        // StandardServletMultipartResolver 是Spring提供的一个实现，用于解析multipart/form-data请求
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        return resolver;
    }
}