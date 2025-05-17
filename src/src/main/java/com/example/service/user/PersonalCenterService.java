package com.example.service.user;

import com.example.dto.PasswordUpdateDTO;
import com.example.dto.UserUpdateDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author wzs
 * @date 2025年05月16日 18:34
 */
@Service
public class PersonalCenterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User updateBasicInfo(Integer userId, UserUpdateDTO updateDTO) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("用户不存在"));

        user.setUsername(updateDTO.getUsername());
        user.setNumber(updateDTO.getNumber());
        user.setUpdateAt(LocalDateTime.now());
        user.setBio(updateDTO.getBio());

        return userRepository.save(user);

    }

    @Transactional
    public void updatePassword(Integer userId, PasswordUpdateDTO passwordDTO) throws BadRequestException {
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("用户不存在"));
        
        //验证旧密码是否正确
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPasswordHash())){
            throw new BadRequestException("旧密码错误");
        }

        //验证新密码和确认密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())){
            throw new BadRequestException("新密码和确认密码不一致");
        }

        //对新密码就行加密并更新
        String encodeNewPassWord = passwordEncoder.encode(passwordDTO.getNewPassword());
        user.setPasswordHash(encodeNewPassWord);
        user.setUpdateAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Transactional
    public void updatePhoto(Integer userId, MultipartFile file) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("用户不存在"));
        user.setPhoto(file.getBytes());
        user.setUpdateAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User getUserProfile(Integer userId) throws BadRequestException {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException("用户不存在"));
    }
}
