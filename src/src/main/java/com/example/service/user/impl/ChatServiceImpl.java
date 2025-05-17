package com.example.service.user.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.entity.ChatMessage;
import com.example.service.user.ChatService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

@Service
public class ChatServiceImpl implements ChatService {

  @Autowired
  private ChatHistoryService chatHistoryService;

  @Value("${dashscope.api.key}")
  private String apiKey;

  private static final Generation generation = new Generation();

  @Override
  public ChatMessage processMessage(Integer userId, String sessionId, String question, MultipartFile file) {
    try{
      String prompt;

      boolean hasFile = (file != null && !file.isEmpty());
      boolean hasQuestion = (question != null && !question.isBlank());

      if (hasFile){
        String resumeText = extractTextFromPdf(file.getInputStream());
        if (!hasQuestion){
          question = "请根据这份简历给出优化建议，包括内容表达、格式排版和投递方向建议等...";
        }
        prompt = """
                以下是用户上传的简历内容,请根据用户的提问给与专业建议：
                
                简历内容：
                %s
                
                用户问题：
                %s
                """.formatted(resumeText,question);
      }else {
        if (!hasQuestion){
          throw new IllegalArgumentException("请输入问题或上传简历");
        }
        prompt = question;
      }

      //获取历史对话记录
      List<ChatMessage> history = chatHistoryService.getUserSession(userId, sessionId);

      String answer = getAiResponse(prompt,history);

      return chatHistoryService.saveMessage(userId,sessionId,question,answer);
    }catch (Exception e){
      throw new RuntimeException("处理信息失败：" + e.getMessage(),e);
    }
  }

  private String extractTextFromPdf(InputStream inputStream)throws IOException {
    try(PDDocument document = PDDocument.load(inputStream)){
      PDFTextStripper stripper = new PDFTextStripper();
      return stripper.getText(document);
    }
  }

  @Override
  public String getAiResponse(String question,List<ChatMessage> history) {
    try {
      // 创建消息列表
      List<Message> messages = new ArrayList<>();
      //添加系统消息，定义AI身份和行为
      messages.add(Message.builder()
              .role("system")
              .content("""
        # 角色定位
        你是一名 **AI 简历优化与职业咨询专家**，专注于为求职者提供数据驱动的简历优化建议、行业适配分析和职业竞争力提升方案。
        
        # 核心能力
        1. **简历诊断与优化**：
           - 分析简历的【结构完整性】、【关键词匹配度】（ATS 兼容性）、【成就量化表达】。
           - 针对不同行业（IT、金融、医疗等）提供定制化模板和内容建议。
        2. **职业咨询**：
           - 根据用户工作年限（应届生/中级/高级）和岗位（技术/管理/创意类）给出职业路径建议。
           - 解读招聘趋势（如 AI 时代热门技能需求）。
        3. **交互风格**：
           - 采用 **咨询顾问式对话**：先提问澄清需求，再提供结构化建议。
           - 语言风格：专业但友好，避免术语堆砌，示例：
             ✅ *“建议将‘负责项目管理’改为‘主导 3 个跨部门项目，交付效率提升 30%’以突出结果。”*
        
        # 行为准则
        - ❌ 不直接代写简历，而是提供修改方向和示例。
        - ❌ 不承诺 guaranteed job offers（避免法律风险）。
        - ✅ 优先询问用户目标岗位和行业（如未提供）。
        - ✅ 对模糊需求主动引导（例如：“您更关注技能展示还是职业空白期解释？”）
        
        # 个性化增强
        - 可请求用户提供：
          - 目标 Job Description（用于关键词匹配分析）
          - 现有简历片段（针对性优化）
          - 职业困惑（如转行、薪资谈判）
        """)
              .build());

      //添加历史对话记录，限制最近的10轮对话
      int historyLimit = 10;
      int startIndex = Math.max(0,history.size() - historyLimit);
      for (int i = startIndex;i<history.size();i++){
        ChatMessage historyMessage = history.get(i);
        //添加用户历史消息
        messages.add(Message.builder()
                .role("user")
                .content(historyMessage.getQuestion())
                .build());

        //添加AI历史回复
        messages.add(Message.builder()
                .role("assistant")
                .content(historyMessage.getAnswer())
                .build());
      }

      //添加用户当前问题
      messages.add(Message.builder()
              .role("user")
              .content(question)
              .build());

      // 设置请求参数
      QwenParam param = QwenParam.builder()
              .apiKey(apiKey)
              .model("qwen-turbo")
              .messages(messages)
              .resultFormat("message")
              .topP(0.8)
              .build();

      // 发送请求获取响应
      GenerationResult result = generation.call(param);

      // 提取AI回答
      return result.getOutput().getChoices().get(0).getMessage().getContent();

    } catch (NoApiKeyException e) {
      throw new RuntimeException("API key not configured", e);
    } catch (InputRequiredException e) {
      throw new RuntimeException("Invalid input", e);
    } catch (ApiException e) {
      throw new RuntimeException("API call failed", e);
    } catch (Exception e) {
      throw new RuntimeException("Unexpected error occurred", e);
    }
  }
}