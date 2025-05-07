package com.example.controller.user;

import com.example.dto.ResumeDTO;
import com.example.entity.User;
import com.example.entity.resume.Resume;
import com.example.repository.ResumRepository;
import com.example.repository.UserRepository;
import com.example.service.user.UserResumeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author wzs
 * @date 2025年05月05日 19:53
 */
@RestController
@RequestMapping("/api/user/resumes")
@Slf4j
public class UserResumeController {

    @Autowired
    private ResumRepository resumRepository;

    @Autowired
    private UserResumeService resumeService;

    @Autowired
    private UserRepository userRepository;

    public UserResumeController(ResumRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @GetMapping
    public ResponseEntity<List<ResumeDTO>> getResume(){
        Integer user = getUserId();
        List<ResumeDTO> resume = resumeService.getResume(user);
        return ResponseEntity.ok(resume);
    }

    private Integer getUserId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.getUserByEmail(email);
        if (user == null){
            throw new RuntimeException("用户不存在");
        }
        return user.getId();
    }

    @GetMapping("/pdf/{id}")
    public void getResumePdf(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        File file = resumeService.getResumePdfFile(id);
        //如果文件不存在，返回404状态码
        if(file == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //设置响应头，包括内容类型和文件名
        String name = file.getName();
        String encodedFile = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
        response.setContentType("application/pdf");
        //Content-Disposition = inline：表示在浏览器内联打开，而不是下载（如果写出attachment，会变成强制下载）
        response.setHeader("Content-Disposition","inline;filename="+encodedFile);

        //输出文件流到客户端
        try(FileInputStream fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream()){
            byte[] buffer = new byte[1024];
            int len;
            while((len = fis.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Integer id){
        resumeService.deleResume(id);
        return ResponseEntity.ok().build();
    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    private final ResumRepository resumeRepository;

    @PostMapping("/save")
    public ResponseEntity<?> saveResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("templateName") String templateName) {
        try {
            // 1. 处理文件上传
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // 2. 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. 保存文件
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. 保存数据库记录
            Resume resume = new Resume();
            // 初始化User对象并设置ID
            User user = new User();
            user.setId(getUserId());
            resume.setUser(user);
            resume.setTitle(title);
            resume.setTemplateName(templateName);
            resume.setFileName(uniqueFileName);
            resume.setOriginalFileName(originalFilename);
            resume.setFilePath(filePath.toString());
            resume.setFileSize(file.getSize());
            resume.setCreatedAt(LocalDateTime.now());
            resume.setUpdatedAt(LocalDateTime.now());

            Resume savedResume = resumeRepository.save(resume);
            return ResponseEntity.ok(savedResume);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("保存简历失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("保存简历失败: " + e.getMessage());
        }
    }

    @Data
    public static class ResumeRequest{
        private String title;
        private String templateName;
        private String pdfPath;
    }
}
