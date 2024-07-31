package org.example.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.exception.SocialException;
import org.example.tiktok.mapper.SocialMapper;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.pojo.entity.Social;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.PageVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {


    private final SocialMapper socialMapper;
    private final UserMapper userMapper;

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
            if (user1.getFolloweeId().equals(toUserId)) {
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

    //根据 user_id 查看指定人的关注列表
    @Override
    public Result ListFollowee(String userId, Page<User> page) {
        LambdaQueryWrapper<Social> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Social::getFollowerId, userId);

        Page<Social> socialPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<Social> socialPage1 = socialMapper.selectPage(socialPage, queryWrapper);

        List<User> followees = socialPage1.getRecords().stream()
                .map(social -> userMapper.selectById(social.getFolloweeId()))
                .collect(Collectors.toList());

        PageVO<User> userPage = new PageVO<>();
        userPage.setItems(followees);
        userPage.setTotal(socialPage.getTotal());


        return Result.success(userPage);
    }

    //根据 user_id 查看指定人的粉丝列表
    @Override
    public Result ListFollower(String userId, Page<User> page) {
        LambdaQueryWrapper<Social> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Social::getFolloweeId, userId);

        Page<Social> socialPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<Social> socialPage1 = socialMapper.selectPage(socialPage, queryWrapper);

        List<User> followers = socialPage1.getRecords().stream()
                .map(social -> userMapper.selectById(social.getFollowerId()))
                .collect(Collectors.toList());

        PageVO<User> userPage = new PageVO<>();
        userPage.setItems(followers);
        userPage.setTotal(socialPage.getTotal());

        return Result.success(userPage);
    }

    @Override
    public Result ListFriends(String userId, Page<User> page) {
        // 获取当前用户关注的人
        LambdaQueryWrapper<Social> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(Social::getFollowerId, userId);
        List<String> followedUserIds = socialMapper.selectList(followWrapper).stream()
                .map(Social::getFolloweeId)
                .collect(Collectors.toList());

        // 获取关注当前用户的人
        LambdaQueryWrapper<Social> followerWrapper = new LambdaQueryWrapper<>();
        followerWrapper.eq(Social::getFolloweeId, userId);
        List<String> followerUserIds = socialMapper.selectList(followerWrapper).stream()
                .map(Social::getFollowerId)
                .collect(Collectors.toList());

        // 找出互相关注的人
        List<String> mutualFollowUserIds = followedUserIds.stream()
                .filter(followerUserIds::contains)
                .collect(Collectors.toList());

        // 根据互相关注的ID获取用户信息
        List<User> friends = mutualFollowUserIds.stream()
                .map(id -> userMapper.selectById(id))
                .collect(Collectors.toList());

        PageVO<User> userPage = new PageVO<>();
        userPage.setItems(friends);
        userPage.setTotal(friends.size());

        return Result.success(userPage);
    }
}
