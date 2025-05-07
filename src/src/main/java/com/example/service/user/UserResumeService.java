package com.example.service.user;

import com.example.dto.ResumeDTO;
import com.example.entity.User;
import com.example.entity.resume.Resume;
import com.example.repository.ResumRepository;
import com.example.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.Document;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* @author wzs
* @date 2025年05月05日 20:48
*/
@Service
public class UserResumeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumRepository resumRepository;

    public List<ResumeDTO> getResume(Integer id){
        List<Resume> resume = resumRepository.findByUserId(id);
        return  resume.stream()
                .map(this::resumeToDTO)
                .collect(Collectors.toList());
    }
    private ResumeDTO resumeToDTO(Resume resume) {
        ResumeDTO dto = new ResumeDTO();
        dto.setId(resume.getId());
        dto.setUserName(resume.getUser().getUsername());
        dto.setTitle(resume.getTitle());
        dto.setUpdateTime(resume.getUpdatedAt());
        return dto;
    }

    public File getResumePdfFile(Integer id){
        Resume resume = resumRepository.findById(id).orElse(null);
        if(resume == null || resume.getFilePath() == null){
            return null;
        }
        File file = new File(resume.getFilePath());
        return file.exists() ? file : null;
    }

    @Transactional    //声明事务性操作
    public void deleResume(Integer id){
        Optional<Resume> resume = resumRepository.findById(id);
        if (resume.isPresent()){
            String filePath = resume.get().getFilePath();
            if (filePath != null){
                File file = new File(filePath);
                if (file.exists()){
                    file.delete();
                }
            }
            resumRepository.deleteById(id);
        }
    }
}
