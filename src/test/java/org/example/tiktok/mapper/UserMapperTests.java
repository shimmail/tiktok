package org.example.tiktok.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.tiktok.pojo.entity.User;
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
}
