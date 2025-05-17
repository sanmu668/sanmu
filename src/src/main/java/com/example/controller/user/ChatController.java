package com.example.controller.user;

import com.example.entity.ChatMessage;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.user.impl.ChatHistoryService;
import com.example.service.user.ChatService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

  @Autowired
  private ChatHistoryService chatHistoryService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChatService chatService; // AI对话服务

  @PostMapping("/new-session")
  public ResponseEntity<SessionResponse> createNewSession() {
    String sessionId = UUID.randomUUID().toString();
    return ResponseEntity.ok(new SessionResponse(sessionId));
  }

  @PostMapping(value = "/message",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ChatMessage> sendMessage(
          @RequestParam(value = "question",required = false) String question,
          @RequestParam(value = "sessionId") String sessionId,
          @RequestParam(value = "file",required = false)MultipartFile file) {
    // 调用AI服务获取回答
    ChatMessage message = chatService.processMessage(getUserId(), sessionId, question, file);
    return ResponseEntity.ok(message);
  }

  @GetMapping("/history")
  public ResponseEntity<List<ChatMessage>> getHistory(
          @RequestParam(required = false) String sessionId) {
    List<ChatMessage> history;
    if (sessionId != null) {
      history = chatHistoryService.getUserSession(getUserId(), sessionId);
    } else {
      history = chatHistoryService.getUserHistory(getUserId());
    }
    return ResponseEntity.ok(history);
  }

  @GetMapping("/recent")
  public ResponseEntity<List<ChatMessage>> getRecentHistory(@RequestParam(defaultValue = "10") int limit) {
    List<ChatMessage> history = chatHistoryService.findTopNByUserIdOrderByTimestampDesc(getUserId(), limit);
    return ResponseEntity.ok(history);
  }

  private Integer getUserId(){
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.getUserByEmail(email);
    if (user == null){
      throw new RuntimeException("用户不存在");
    }
    return user.getId();
  }
}

// 请求和响应DTO
@Data
class ChatRequest {
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