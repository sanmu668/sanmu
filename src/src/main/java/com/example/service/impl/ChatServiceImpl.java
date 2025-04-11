package com.example.service.impl;

import com.example.entity.ChatMessage;
import com.example.service.ChatService;
import com.example.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatService接口的实现类
 * 使用DashScope SDK实现与AI模型的交互
 */
@Service
public class ChatServiceImpl implements ChatService {

  private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

  @Autowired
  private ChatHistoryService chatHistoryService;

  /**
   * DashScope API密钥，从配置文件中注入
   */
  @Value("${dashscope.api.key}")
  private String apiKey;

  /**
   * DashScope 模型名称
   */
  @Value("${dashscope.model}")
  private String model;

  /**
   * 初始化方法，在服务启动时设置API密钥
   */
  @PostConstruct
  public void init() {
    if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_API_KEY_HERE".equals(apiKey)) {
      throw new IllegalStateException("DashScope API密钥未配置，请在application.properties中设置dashscope.api.key");
    }
    logger.info("正在初始化DashScope配置...");
    System.setProperty("dashscope.api.key", apiKey);
    logger.info("DashScope配置初始化完成");
  }

  /**
   * 处理用户消息并返回AI响应
   */
  @Override
  public ChatMessage processMessage(String message, String sessionId) {
    try {
      // 创建用户消息并添加到历史记录
      ChatMessage userMessage = ChatMessage.createUserMessage(sessionId, message);
      chatHistoryService.addMessage(userMessage);

      // 创建通用文本生成器实例
      Generation gen = new Generation();

      // 构建消息列表，包含历史记录
      List<Message> messages = new ArrayList<>();

      // 添加系统角色设定
      messages.add(Message.builder()
          .role(Role.SYSTEM.getValue())
          .content("你是一位专业的智能简历顾问，擅长简历优化、职业规划和面试辅导。你需要：\n" +
              "1. 帮助用户分析和优化简历内容\n" +
              "2. 根据用户背景提供个性化的职业发展建议\n" +
              "3. 模拟面试场景，提供面试技巧指导\n" +
              "4. 结合行业趋势，推荐适合的职业方向\n" +
              "5. 以专业、友好的态度与用户交流，给出具体、可行的建议")
          .build());

      // 添加历史消息
      for (ChatMessage historyMessage : chatHistoryService.getSessionHistory(sessionId)) {
        messages.add(Message.builder()
            .role(historyMessage.getType().equals("user") ? Role.USER.getValue() : Role.ASSISTANT.getValue())
            .content(historyMessage.getContent())
            .build());
      }

      // 设置模型参数
      QwenParam param = QwenParam.builder()
          .model(model)
          .messages(messages)
          .resultFormat(QwenParam.ResultFormat.MESSAGE)
          .apiKey(apiKey) // 显式设置API密钥
          .build();

      // 调用API获取响应
      GenerationResult result = gen.call(param);

      // 构建响应消息对象
      ChatMessage response = ChatMessage.createAiMessage(
          sessionId,
          result.getOutput().getChoices().get(0).getMessage().getContent());

      // 将AI响应添加到历史记录
      chatHistoryService.addMessage(response);

      return response;

    } catch (NoApiKeyException e) {
      logger.error("DashScope API密钥未找到或无效", e);
      throw new RuntimeException("AI服务配置错误，请联系管理员", e);
    } catch (InputRequiredException e) {
      logger.error("输入参数无效", e);
      throw new RuntimeException("输入参数无效", e);
    } catch (ApiException e) {
      logger.error("API调用失败", e);
      throw new RuntimeException("AI服务暂时不可用，请稍后重试", e);
    }
  }

  /**
   * 创建新的会话
   */
  @Override
  public String createNewSession() {
    String sessionId = String.valueOf(System.currentTimeMillis());
    return sessionId;
  }
}