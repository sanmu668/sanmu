package com.example.service.user;

import com.example.entity.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {
  ChatMessage processMessage(Integer userId, String sessionId, String question, MultipartFile file);

  String getAiResponse(String question, List<ChatMessage> history);
}