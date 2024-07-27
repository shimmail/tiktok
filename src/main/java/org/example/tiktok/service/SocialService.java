package org.example.tiktok.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.result.Result;

public interface SocialService {
    Result follow(String id, String toUserId);

    Result unfollow(String id, String toUserId);

    //根据 user_id 查看指定人的关注列表
    Result ListFollowee(String userId, Page<User> page);

    //根据 user_id 查看指定人的粉丝列表
    Result ListFollower(String userId, Page<User> page);

    Result ListFriends(String userId, Page<User> page);
}
