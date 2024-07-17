package org.example.tiktok.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public Result register(@RequestParam("username") String username,
                           @RequestParam("password") String password) {
        try {
            log.info("Registering user: {}", username);
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setPassword(password);
            return userService.register(userDTO);
        } catch (Exception e) {
            log.error("Failed to register user: {}", username, e);
            return Result.error(e.getMessage());
        }
    }
}
