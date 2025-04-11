package com.example.service;

import com.example.entity.ChatMessage;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天历史记录管理服务
 */
@Service
public class ChatHistoryService {

  /**
   * 使用ConcurrentHashMap存储会话历史记录
   * key: sessionId
   * value: 该会话的消息列表
   */
  private final Map<String, List<ChatMessage>> sessionHistories = new ConcurrentHashMap<>();

  /**
   * 添加消息到历史记录
   */
  public void addMessage(ChatMessage message) {
    String sessionId = message.getSessionId();
    sessionHistories.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);

    // 保持最近的10条消息
    List<ChatMessage> history = sessionHistories.get(sessionId);
    if (history.size() > 10) {
      sessionHistories.put(sessionId, history.subList(history.size() - 10, history.size()));
    }
  }

  /**
   * 获取会话的历史记录
   */
  public List<ChatMessage> getSessionHistory(String sessionId) {
    return sessionHistories.getOrDefault(sessionId, new ArrayList<>());
  }

  /**
   * 清除会话历史
   */
  public void clearSessionHistory(String sessionId) {
    sessionHistories.remove(sessionId);
  }
}