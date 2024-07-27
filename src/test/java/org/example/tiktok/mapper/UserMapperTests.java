package org.example.tiktok.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.util.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserMapperTests {
    @Autowired
    UserMapper userMapper;
    @Test
    public void testUserMapper(){
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }
    @Test
    public void testToken(){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsImlkIjoiaWQiLCJleHAiOjE3MjE2NTI2NjN9.C2lbk-GOOTe3RUsUjTnEPn0_T0bXtpumpLG469FOw78";
        System.out.println(JWTUtils.getId(token));
    }


}
