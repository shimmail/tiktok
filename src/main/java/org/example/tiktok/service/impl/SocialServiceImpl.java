package org.example.tiktok.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.exception.SocialException;
import org.example.tiktok.mapper.SocialMapper;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.pojo.entity.Social;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class SocialServiceImpl implements SocialService {

    @Autowired
    SocialMapper socialMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    @Transactional
    public Result follow(String id, String toUserId) {
        if (id.equals(toUserId)) {
            throw new SocialException("不能关注自己");
        }
        User user = userMapper.selectById(toUserId);
        if (user == null) {
            throw new SocialException("用户不存在");
        }

        List<Social> users = socialMapper.selectFollowingByUserId(id);
        for (Social user1 : users) {
            if (user1.getId().equals(toUserId)) {
                throw new SocialException("无法重复关注");
            }
        }

        Social follow = new Social();
        follow.setFollowerId(id);
        follow.setFolloweeId(toUserId);
        follow.setCreatedAt(new java.util.Date());
        socialMapper.insert(follow);
        return Result.success();
    }

    @Override
    @Transactional
    public Result unfollow(String id, String toUserId) {
        User user = userMapper.selectById(toUserId);
        if (user == null) {
            throw new SocialException("用户不存在");
        }

        if (id.equals(toUserId)) {
            throw new SocialException("不能取关自己");
        }
        List<Social> users = socialMapper.selectFollowingByUserId(id);
        for (Social user1 : users) {
            if ((user1.getFolloweeId().equals(toUserId))) {
                socialMapper.unfollow(id, toUserId);
                return Result.success();
            }
        }
        throw new SocialException("未关注该用户");


    }
}
