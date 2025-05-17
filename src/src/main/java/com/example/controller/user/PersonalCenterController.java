package com.example.controller.user;

import com.example.dto.PasswordUpdateDTO;
import com.example.dto.UserUpdateDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.user.PersonalCenterService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author wzs
 * @date 2025年05月16日 22:40
 */
@RestController
@RequestMapping("/api/user/personal")
@RequiredArgsConstructor
public class PersonalCenterController{

    @Autowired
    private final PersonalCenterService personalService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() throws BadRequestException {
        return ResponseEntity.ok(personalService.getUserProfile(getUserId()));
    }

    @PutMapping("/basic-info")
    public ResponseEntity<User> updateBasicInfo(@RequestBody UserUpdateDTO updateDTO) throws BadRequestException {
        return ResponseEntity.ok(personalService.updateBasicInfo(getUserId(), updateDTO));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateDTO passwordDTO) throws BadRequestException {
        personalService.updatePassword(getUserId(), passwordDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/photo")
    public ResponseEntity<Void> updatePhoto(@RequestParam("file") MultipartFile file) throws IOException {
        personalService.updatePhoto(getUserId(), file);
        return ResponseEntity.ok().build();
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
