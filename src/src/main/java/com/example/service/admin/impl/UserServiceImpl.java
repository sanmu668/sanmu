package com.example.service.admin.impl;

import com.example.dto.UserDTO;
import com.example.dto.UserSearchRequest;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.admin.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;


/**
 * @author wzs
 * @date 2025年04月30日 22:45
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepository.save(user);
    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO userDTO){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNumber(userDTO.getNumber());
        userRepository.save(user);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNumber(user.getNumber());
        return dto;
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserDTO> searchUsers(UserSearchRequest request){
        PageRequest pageRequest = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<User> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getUsername())){
                predicates.add(cb.like(root.get("username"),"%" +request.getUsername() + "%"));
            }
            if (StringUtils.hasText(request.getEmail())){
                predicates.add(cb.like(root.get("email"),"%" + request.getEmail() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> users = userRepository.findAll(spec, pageRequest);
        return users.map(user -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user,dto);
            return dto;
        });
    }

    @Override
    public User findByEamil(String email) {
        return userRepository.getUserByEmail(email);
    }
}
