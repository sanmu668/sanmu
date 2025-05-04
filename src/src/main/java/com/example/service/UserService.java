package com.example.service;

import com.example.dto.UserDTO;
import com.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    UserDTO updateUser(Integer id, UserDTO userDTO);

    void deleteUser(Integer id);

    List<UserDTO> getAllUsers();

    List<UserDTO> getByUsername(String username);

    Optional<UserDTO> getByEamil(String email);
}
