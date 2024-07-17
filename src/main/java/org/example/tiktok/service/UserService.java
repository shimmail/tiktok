package org.example.tiktok.service;

import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;

public interface UserService {


    Result register(UserDTO userDTO);

    UserVO login(UserDTO userDTO);

    UserVO getUserById(String id);
}
