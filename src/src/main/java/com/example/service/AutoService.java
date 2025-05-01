package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author wzs
 * @date 2025年04月28日 0:06
 */
@Service
@RequiredArgsConstructor   //自动生成构造函数
public class AutoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 注册
     *
     * @param request 注册请求对象，包含用户输入的邮箱和密码
     * @return 返回通过JWT工具生成的用户令牌
     */
    public String register(RegisterRequest request){
        //检查邮箱是否已经存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }
        //创建新的用户对象
        User user = new User();
        user.setEmail(request.getEmail());
        //哈希加密
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        //设置用户创建和更新的时间
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());

        //保存用户到数据库
        userRepository.save(user);
        return jwtUtils.genrateToken(user.getEmail(), String.valueOf(user.getRole()));
    }

    /**
     * 登录
     * @param request 包含用户输入的邮箱和密码的登录请求对象
     * @return 登录成功后生成的JWT令牌
     */
    public String login(LoginRequest request){
        //验证邮箱
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        //验证密码
        if (!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new RuntimeException("Invalid email or password");
        }
        //更新用户最后登入时间，并保存更新
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return jwtUtils.genrateToken(user.getEmail(), String.valueOf(user.getRole()));
    }
}
