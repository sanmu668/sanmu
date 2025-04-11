package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用程序的主入口类
 * 
 * @SpringBootApplication 注解表明这是一个Spring Boot应用程序
 *                        它包含了以下功能：
 *                        - @Configuration: 标记这个类为配置类
 *                        - @EnableAutoConfiguration: 启用Spring Boot的自动配置机制
 *                        - @ComponentScan: 启用组件扫描，自动发现和注册Bean
 */
@SpringBootApplication
public class Application {

  /**
   * 应用程序的主入口方法
   * 
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}