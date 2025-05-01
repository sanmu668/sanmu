package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 * 用于封装聊天消息的相关信息
 */
@Data     //自动生成get、set方法
@Entity   //标识该类为JPA实体
@Table(name = "chat_message")
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)  //主键自增
  private Long id;

  @Column(name = "user_id" ,nullable = false)
  private Integer userId;

  @Column(name = "session_id" ,nullable = false)
  private String sessionId;

  @Column(name = "question",nullable = false,columnDefinition = "TEXT")
  private String question;

  @Column(name = "answer",nullable = false,columnDefinition = "TEXT")
  private String answer;

  @Column(name = "timestamp")
  private LocalDateTime timestamp;

  @PrePersist
  protected void onCreate(){
    timestamp = LocalDateTime.now();
  }

  public ChatMessage(){}

  public ChatMessage(Integer userId,String sessionId,String question,String answer){
    this.userId = userId;
    this.sessionId = sessionId;
    this.question = question;
    this.answer = answer;
  }
}