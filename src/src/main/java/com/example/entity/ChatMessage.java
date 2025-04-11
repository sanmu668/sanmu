package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 * 用于封装聊天消息的相关信息
 */
@Data
public class ChatMessage {

  /**
   * 消息ID，可为空
   */
  private String id;

  /**
   * 会话ID，用于关联同一会话中的消息
   */
  private String sessionId;

  /**
   * 消息内容
   */
  private String content;

  /**
   * 消息类型，例如："user"表示用户消息，"ai"表示AI响应
   */
  private String type;

  /**
   * 消息时间戳
   */
  private LocalDateTime timestamp;
  /**
   * 创建用户类型聊天消息对象并初始化属性
   *
   * @param sessionId 会话唯一标识符，用于关联特定对话上下文
   * @param content 用户消息的文本内容，支持包含富文本格式
   * @return 完整初始化的ChatMessage对象实例，包含：
   *         - 预设的用户消息类型（"user"）
   *         - 自动生成的当前时间戳
   *         - 关联的会话ID
   *         - 用户输入的内容
   */
  public static ChatMessage createUserMessage(String sessionId, String content) {
    // 初始化消息对象并设置基础属性
    ChatMessage message = new ChatMessage();
    message.setSessionId(sessionId);
    message.setContent(content);
    message.setType("user");
    message.setTimestamp(LocalDateTime.now());
    return message;
  }


  /**
   * 创建并返回一个AI生成的聊天消息对象
   *
   * @param sessionId 消息关联的会话ID，用于标识所属对话上下文
   * @param content   消息文本内容，包含AI生成的回复内容
   * @return ChatMessage 配置完成的聊天消息对象，包含：
   *         - 会话ID
   *         - 消息内容
   *         - 消息类型固定为"ai"
   *         - 自动生成的时间戳
   */
  public static ChatMessage createAiMessage(String sessionId, String content) {
    ChatMessage message = new ChatMessage();

    // 设置基础消息属性
    message.setSessionId(sessionId);
    message.setContent(content);

    // 标记消息类型为AI生成，并记录创建时间
    message.setType("ai");
    message.setTimestamp(LocalDateTime.now());

    return message;
  }

}