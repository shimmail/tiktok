package org.example.tiktok.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.util.JWTUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.notification.RunListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.SortedMap;

@SpringBootTest
public class MapperTests {
    @Autowired
    UserMapper userMapper;
    @Autowired
    VideoMapper videoMapper;
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

    @Test
    public void testListPopular(){
        Page<Video> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);

        QueryWrapper<Video> queryWrapper = new QueryWrapper<Video>()
                .orderByDesc("visit_count");  // 按访问量降序排序

        Page<Video> videoPage = videoMapper.selectPage(page, queryWrapper);
        List<Video> videos = videoPage.getRecords();

        for (Video video : videos) {
            System.out.println(video);
        }
    }

    @Test
    public void testSelectByUsernameLike(){
        List<User> users = userMapper.selectByUsernameLike("a");
        for (User user : users){
            System.out.println(user);
        }
    }
}
