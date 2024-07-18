package org.example.tiktok.controller;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.exception.PasswordErrorException;
import org.example.tiktok.exception.UserNotFoundException;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    //用户注册
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
            log.error("注册失败");
            return Result.error(e.getMessage());
        }
    }

    //用户登录
    @PostMapping("/login")
    public Result<UserVO> login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {
        Map<String,Object> map = new HashMap<>();
        UserVO userVO = new UserVO();
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setPassword(password);
            userVO = userService.login(userDTO);
        }catch (Exception e) {
        return Result.error(e.getMessage());
    }
        try {
            //登录成功后生成token
            Map<String, String> payload = new HashMap<>();
            payload.put("name", userVO.getUsername());
            //生成JWT令牌
            String token = JWTUtils.getToken(payload);
            map.put("state",true);
            map.put("msg","认证成功");
            map.put("token",token);//响应token
            log.info("登录成功, token: {}", token);
            return Result.success(userVO);
        }catch (Exception e) {
            map.put("state","false");
            map.put("msg",e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    //根据id查询用户
    @GetMapping("/info")
    public Result<UserVO> getStudentById(@RequestParam("user_id") String id,
                                         @RequestHeader("Access-Token") String accessToken){
        UserVO userVO = new UserVO();
        // 获取用户信息
        try {
            userVO = userService.getUserById(id);
        }catch (Exception e){
            return Result.error(e.getMessage());
        }

        // 验证Access-Token
        try {
            JWTUtils.verify(accessToken);
            return Result.success(userVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
