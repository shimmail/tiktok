package org.example.tiktok;

import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class TiktokApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        System.out.println(("----- insert method test ------"));
        User user = new User();
        user.setUsername("test");
        String password = "1234";
        user.setPassword(PasswordUtil.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        System.out.println(user);
    }


}
