package com.example.controller;

import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.AutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author wzs
 * @date 2025年04月28日 0:21
 */
@RestController
@RequestMapping("api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AutoContrdoller {
    private final AutoService autoService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request){
        String token = autoService.register(request);
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){
        String token = autoService.login(request);
        return new AuthResponse(token);
    }
}
