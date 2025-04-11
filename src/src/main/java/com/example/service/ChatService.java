package com.example.service;

import com.example.entity.ChatMessage;

/**
 * 聊天服务接口
 * 定义了处理聊天消息的核心业务逻辑
 */
public interface ChatService {

  /**
   * 处理用户发送的消息
   * 
   * @param message   用户发送的消息内容
   * @param sessionId 会话ID，用于维护对话上下文
   * @return 返回AI的响应消息
   */
  ChatMessage processMessage(String message, String sessionId);

  String createNewSession();
}