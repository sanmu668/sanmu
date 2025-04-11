package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DashScope配置类
 * 负责初始化DashScope SDK的配置
 */
@Configuration
public class DashScopeConfig {

  private static final Logger logger = LoggerFactory.getLogger(DashScopeConfig.class);

  /**
   * DashScope API密钥
   * 从配置文件中通过@Value注解注入
   */
  @Value("${dashscope.api.key}")
  private String apiKey;

  @Value("${spring.ai.dashscope.chat.options.model}")
  private String model;

  /**
   * 初始化方法
   * 在Spring容器启动时自动执行
   * 设置DashScope SDK的API密钥
   */
  @PostConstruct
  public void init() {
    if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_API_KEY_HERE".equals(apiKey)) {
      logger.error("DashScope API密钥未配置！请在application.properties中设置dashscope.api.key");
      throw new IllegalStateException("请在application.properties中配置正确的DashScope API密钥");
    }

    logger.info("正在初始化DashScope配置...");
    try {
      System.setProperty("dashscope.api.key", apiKey);
      logger.info("DashScope配置初始化完成");
    } catch (Exception e) {
      logger.error("DashScope配置初始化失败", e);
      throw new IllegalStateException("DashScope配置初始化失败", e);
    }
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getModel() {
    return model;
  }
}