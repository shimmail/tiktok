package org.example.tiktok.controller;

import com.aliyun.oss.OSS;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private OSS ossClient;

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
        Map<String, Object> map = new HashMap<>();
        UserVO userVO = new UserVO();
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setPassword(password);
            userVO = userService.login(userDTO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
        try {
            //登录成功后生成token
            //生成JWT令牌
            String token = JWTUtils.getToken(userVO.getUsername(), userVO.getId());
            log.info("登录成功, token: {}", token);
            return Result.success(userVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    //根据id查询用户
    @GetMapping("/info")
    public Result<UserVO> getStudentById(@RequestParam("user_id") String id) {
        UserVO userVO = new UserVO();
        try {
            // 获取用户信息
            userVO = userService.getUserById(id);
            log.info("查询成功：{}" ,userVO);
            return Result.success(userVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }

    }

    //上传头像
    @PutMapping("/avatar/upload")
    public Result<UserVO> uploadAvatar(@RequestParam("data") MultipartFile avatar,
                                       @RequestHeader("Access-Token") String accessToken) {
        try {
            //解析token获得username
            String id = JWTUtils.getId(accessToken);
            UserVO userVO = userService.uploadAvatar(avatar, id);
            return Result.success(userVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


}
