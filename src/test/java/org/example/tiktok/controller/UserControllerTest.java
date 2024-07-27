package org.example.tiktok.controller;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.example.tiktok.service.impl.UserServiceImpl;
import org.example.tiktok.util.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.SocketImpl;
import java.util.Arrays;
import java.util.List;

import static com.alibaba.druid.sql.ast.SQLPartitionValue.Operator.List;
import static jdk.internal.dynalink.support.Guards.isNull;
import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(UserController.class)
@RunWith(SpringJUnit4ClassRunner.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private OSS ossClient;

    @Test
    void register() throws Exception {
        String username = "ccc";
        String password = "1234";

        when(userService.register(any(UserDTO.class))).thenReturn(Result.success("User registered successfully"));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    public void selectPage() {
        IPage<User> page = new Page<User>();
        page.setCurrent(1);
        page.setSize(2);
        IPage<User> userIPage = userMapper.selectPage(page,null);
        System.out.println(userIPage.getRecords().toString());
    }

    @Test
    public void login(){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("aaa");
        userDTO.setPassword("1234");
        userService.login(userDTO);
    }
}