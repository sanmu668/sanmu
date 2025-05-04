package com.example.controller;

import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.service.AutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author wzs
 * @date 2025年04月28日 0:21
 */
@RestController
@RequestMapping("api/auth")
@CrossOrigin    //跨域请求支持
@RequiredArgsConstructor    //生成构造函数
public class AutoController {
    private final AutoService autoService;

    /**
     * 处理用户注册请求
     *
     * 该方法通过POST请求接收用户注册信息
     * 然后调用autoService的注册方法处理这些信息，生成认证令牌
     * 最后返回一个包含认证令牌的响应对象
     * @param request 包含用户注册信息的请求体
     * @return 返回一个包含认证令牌的响应对象
     */
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
