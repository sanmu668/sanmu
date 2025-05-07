package com.example.service.user;

import com.example.entity.ChatMessage;

public interface ChatService {
  ChatMessage processMessage(Integer userId, String sessionId, String question);

  String getAiResponse(String question);
}