package com.example.service.user.impl;

import com.example.entity.ChatMessage;
import com.example.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 聊天历史记录管理服务
 */
@Service
public class ChatHistoryService {
  @Autowired
  private ChatMessageRepository chatMessageRepository;

  /*
  保存对话记录
   */
  @Transactional
  public ChatMessage saveMessage(Integer userId,String sessionId,String question,String answer){
    ChatMessage message = new ChatMessage(userId, sessionId, question, answer);
    return chatMessageRepository.save(message);
  }

  /*
  获取用户的所有对话记录
   */

  public List<ChatMessage> getUserHistory(Integer userId){
    return chatMessageRepository.findByUserId(userId);
  }


  /*
  根据用户ID和会话ID获取对话记录
   */
  public List<ChatMessage> getUserSession(Integer userId,String sessionId){
    return chatMessageRepository.findByUserIdAndSessionId(userId,sessionId);
  }
}