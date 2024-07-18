package org.example.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.internal.dynalink.support.NameCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.example.tiktok.exception.PasswordErrorException;
import org.example.tiktok.exception.UserNotFoundException;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.example.tiktok.util.AliOSSUtils;
import org.example.tiktok.util.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result register(UserDTO userDTO) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 创建新用户并保存到数据库
        User newUser = new User();
        BeanUtils.copyProperties(userDTO, newUser);
        newUser.setPassword(PasswordUtil.encode(userDTO.getPassword())); // 加密密码
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(newUser);

        return Result.success();
    }

    @Override
    public UserVO login(UserDTO userDTO) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }
        String password = userDTO.getPassword();
        String password1 = user.getPassword();
        UserVO userVO = new UserVO(user.getId(),user.getUsername(),user.getAvatarUrl(),user.getCreatedAt(),user.getUpdatedAt(),user.getDeletedAt());
        // 校验密码
        if(PasswordUtil.matches(password,password1)){
            log.info("登录成功");
            return userVO;
        }
        else {
            throw new PasswordErrorException("密码错误");
        }
    }

    //根据id查询用户
    @Override
    public UserVO getUserById(String id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }
        return new UserVO(user.getId(), user.getUsername(), user.getAvatarUrl(), user.getCreatedAt(), user.getUpdatedAt(), user.getDeletedAt());
    }


    //上传头像
    @Override
    public UserVO uploadAvatar(MultipartFile avatar,String username) throws IOException {
        // 上传头像到OSS
        String avatarUrl =AliOSSUtils.upload(avatar);
        // 更新数据库中的用户头像URL
        userMapper.updateAvatarUrlByUsername(username, avatarUrl);

        // 获取更新后的用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));

        // 创建并返回UserVO对象
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatarUrl(avatarUrl);

        return userVO;
    }
}
