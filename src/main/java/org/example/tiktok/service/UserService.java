package org.example.tiktok.service;

import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {


    Result register(UserDTO userDTO);

    UserVO login(UserDTO userDTO);

    UserVO getUserById(String id);

    UserVO uploadAvatar(MultipartFile avatar,String id) throws IOException;
}
