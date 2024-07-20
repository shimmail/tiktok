package org.example.tiktok.controller;

import com.aliyun.oss.OSS;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.UserDTO;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.UserService;
import org.example.tiktok.util.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(UserController.class)
@RunWith(SpringJUnit4ClassRunner.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
    void login() {

    }

    @Test
    void getStudentById() {

    }

    @Test
    void uploadAvatar() {

    }
}