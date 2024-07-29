package org.example.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.exception.UserNotFoundException;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.entity.LoginUser;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.example.tiktok.util.AliOSSUtil;
import org.example.tiktok.util.JWTUtils;
import org.example.tiktok.util.PasswordUtil;
import org.example.tiktok.util.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.example.tiktok.result.Result.success;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AliOSSUtil aliOSSUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    //分页
    public IPage<User> getUsersByPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, null); // `null` indicates no query conditions
    }

    //注册
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result register(UserDTO userDTO) throws Exception {
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
            if (existingUser != null) {
                throw new Exception("用户名已存在");
            }

            // 创建新用户并保存到数据库
            User newUser = new User();
            BeanUtils.copyProperties(userDTO, newUser);
            newUser.setPassword(PasswordUtil.encode(userDTO.getPassword())); // 加密密码
            newUser.setRole("user");// 默认身份为用户
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(newUser);
            return success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    //登录
    @Override
    public Result<UserVO> login(UserDTO userDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getUsername(),userDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        UserVO uservo = userMapper.selectByUsername(userDTO.getUsername());
        //使用userid生成token
        String token = JWTUtils.getToken(uservo.getId());
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        log.info("token:{}",token);

        //authenticate存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);

        return Result.success(uservo);
    }

    //登出
    @Override
    public Result logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userid = loginUser.getUser().getId();
        redisCache.deleteObject("login:"+userid);
        return success("登出成功");
    }

    //根据id查询用户
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO getUserById(String id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }


    //上传头像
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO uploadAvatar(MultipartFile avatar,String id) throws IOException {

        // 上传头像到OSS
        String avatarUrl =aliOSSUtils.uploadAvatar(avatar);
        // 更新数据库中的用户头像URL
        userMapper.updateAvatarUrlByID(id, avatarUrl);

        // 获取更新后的用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("id", id));

        // 创建并返回UserVO对象
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatarUrl(avatarUrl);
        userVO.setCreatedAt(user.getCreatedAt());
        userVO.setUpdatedAt(user.getUpdatedAt());
        userVO.setDeletedAt(user.getDeletedAt());

        return userVO;
    }

}
