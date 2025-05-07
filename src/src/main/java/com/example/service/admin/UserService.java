package com.example.service.admin;

import com.example.dto.UserDTO;
import com.example.dto.UserSearchRequest;
import com.example.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    UserDTO updateUser(Integer id, UserDTO userDTO);

    void deleteUser(Integer id);

    Page<UserDTO> searchUsers(UserSearchRequest request);


    User findByEamil(String email);
}
