package com.example.controller;

import com.example.dto.ResumeDTO;
import com.example.service.ResumeService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author wzs
 * @date 2025年05月03日 0:17
 */
@RestController
@RequestMapping("/api/admin/resumes")
public class ResumeController {
    @Autowired
    private ResumeService resumeService;

    /**
     * 获取简历列表
     *
     * @param searchQuerdy 搜索查询字符串，用于筛选字符串，默认为null
     * @param page 页码
     * @param size  简历数
     * @return  返回包含分页信息的简历列表
     */
    @GetMapping
    public ResponseEntity<Page<ResumeDTO>> getResumes(
            @RequestParam(required = false) String searchQuerdy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        Page<ResumeDTO> resumes = resumeService.getResumes(searchQuerdy, page, size);
        return ResponseEntity.ok(resumes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Integer id){
        resumeService.deleResume(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据ID下载文件
     *
     * @param id 简历唯一标识符
     * @return  包含简历文件的ResponseEntity
     * @throws FileNotFoundException 如果文件不存在
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadResume(@PathVariable Integer id) throws FileNotFoundException {
        File file = resumeService.downloadResume(id);

        //初始化HTTp响应头
        HttpHeaders headers = new HttpHeaders();
        //设置内容类型为二进制流，浏览器会处理为下载，而不是打开网页
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //设置内容-Disposition，指定文件名，处理文件名的UTF-8编码
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename*=UTF-8''"+file.getName());
        //创建InputStreamResource对象，用于封装文件流
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        //构建并返回ResponseEntity对象，用于HTTP相应
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * 获取简历PDF文件
     *
     * @param id 简历标识符
     * @param response 用于像客户端发送响应上的HTTP对象
     * @throws IOException 如果文件读取或写入响应流式发生错误
     */
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
}
