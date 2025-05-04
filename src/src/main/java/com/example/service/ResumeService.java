package com.example.service;

import com.example.dto.ResumeDTO;
import com.example.entity.EducationExperience;
import com.example.entity.Resume;
import com.example.repository.ResumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wzs
 * @date 2025年05月03日 0:03
 */
@Service
public class ResumeService {
    @Autowired
    private ResumRepository resumRepository;

    /**
     * 根据搜索查询获取简历列表
     *
     * @param searchQuery 查询字符串，简历标题
     * @param page  页码
     * @param size  简历数量
     * @return 返回一个ResumeDTO类型的Page对象，包含按条件筛选的简历列表
     */
    public Page<ResumeDTO> getResumes(String searchQuery,int page,int size){
        Page<Resume> resumes;
        if (searchQuery != null && !searchQuery.isEmpty()){
            resumes = resumRepository.findByTitleContaining(searchQuery, PageRequest.of(page -1,size));
        }else {
            resumes = resumRepository.findAll(PageRequest.of(page - 1,size));
        }
        return resumes.map(this::convertToDTO);  //把这个 Page 中的每个 Resume 对象，用 convertToDTO() 方法处理，生成一个 ResumeDTO。
    }

    private ResumeDTO convertToDTO(Resume resume){
        ResumeDTO dto = new ResumeDTO();
        dto.setId(resume.getId());
        dto.setUserName(resume.getUser().getUsername());
        dto.setTitle(resume.getTitle());

        List<String> educationList = resume.getEducationExperience().stream()
                .map(EducationExperience::getDegree)
                .collect(Collectors.toList());
        dto.setEducation(educationList);

        dto.setUpdateTime(resume.getUpdatedAt());
        return dto;
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

    public File downloadResume(Integer id){
        Resume resume = resumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        File file = new File(resume.getFilePath());

        return file.exists() ? file : null;
    }

    public File getResumePdfFile(Integer id){
        Resume resume = resumRepository.findById(id).orElse(null);
        if(resume == null || resume.getFilePath() == null){
            return null;
        }
        File file = new File(resume.getFilePath());
        return file.exists() ? file : null;
    }
}
