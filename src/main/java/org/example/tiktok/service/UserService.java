package org.example.tiktok.service;

import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {


    Result register(UserDTO userDTO) throws Exception;

    Result login(UserDTO userDTO);

    //登出
    Result logout();

    UserVO getUserById(String id) throws Exception;

    UserVO uploadAvatar(MultipartFile avatar,String id) throws IOException;
}
