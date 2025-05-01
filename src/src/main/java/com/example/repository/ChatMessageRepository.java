package com.example.repository;

import com.example.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wzs
 * @date 2025年04月30日 0:56
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 根据用户ID查询聊天记录
    List<ChatMessage> findByUserId(Integer userId);

    // 根据会话ID查询聊天记录
    List<ChatMessage> findBySessionId(String sessionId);

    // 根据用户ID和会话ID查询聊天记录
    List<ChatMessage> findByUserIdAndSessionId(Integer userId, String sessionId);

    // 获取用户最近都是n条对话记录
    @Query("SELECT c FROM ChatMessage c WHERE c.userId = ?1 ORDER BY c.timestamp DESC")
    List<ChatMessage> findByUserIdOrderByTimestampDesc(Integer userId, int n);
}
