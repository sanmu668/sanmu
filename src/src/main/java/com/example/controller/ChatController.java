package com.example.controller;

import com.example.entity.ChatMessage;
import com.example.service.ChatHistoryService;
import com.example.service.ChatService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

  @Autowired
  private ChatHistoryService chatHistoryService;

  @Autowired
  private ChatService chatService; // AI对话服务

  @PostMapping("/new-session")
  public ResponseEntity<SessionResponse> createNewSession(@RequestParam Integer userId) {
    String sessionId = UUID.randomUUID().toString();
    return ResponseEntity.ok(new SessionResponse(sessionId));
  }

  @PostMapping("/message")
  public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatRequest request) {
    // 调用AI服务获取回答
    String answer = chatService.getAiResponse(request.getQuestion());

    // 保存对话记录
    ChatMessage message = chatHistoryService.saveMessage(
            request.getUserId(),
            request.getSessionId(),
            request.getQuestion(),
            answer
    );

    return ResponseEntity.ok(message);
  }

  @GetMapping("/history")
  public ResponseEntity<List<ChatMessage>> getHistory(
          @RequestParam Integer userId,
          @RequestParam(required = false) String sessionId) {
    List<ChatMessage> history;
    if (sessionId != null) {
      history = chatHistoryService.getUserSession(userId, sessionId);
    } else {
      history = chatHistoryService.getUserHistory(userId);
    }
    return ResponseEntity.ok(history);
  }

  @GetMapping("/recent")
  public ResponseEntity<List<ChatMessage>> getRecentHistory(
          @RequestParam Integer userId,
          @RequestParam(defaultValue = "10") int limit) {
    List<ChatMessage> history = chatHistoryService.findTopNByUserIdOrderByTimestampDesc(userId, limit);
    return ResponseEntity.ok(history);
  }
}

// 请求和响应DTO
@Data
class ChatRequest {
  private Integer userId;
  private String sessionId;
  private String question;
}

@Data
class SessionResponse {
  private String sessionId;

  public SessionResponse(String sessionId) {
    this.sessionId = sessionId;
  }
}