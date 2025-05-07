package com.example.controller.admin;

import com.example.dto.UserDTO;
import com.example.dto.UserSearchRequest;
import com.example.entity.User;
import com.example.service.admin.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wzs
 * @date 2025年04月30日 22:51
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updataUser(@PathVariable Integer id,@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.updateUser(id,userDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> searchUsers(UserSearchRequest request){
        return ResponseEntity.ok(userService.searchUsers(request));
    }
}
