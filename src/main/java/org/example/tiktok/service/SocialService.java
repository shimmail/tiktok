package org.example.tiktok.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.result.Result;

public interface SocialService {
    Result follow(String id, String toUserId);

    Result unfollow(String id, String toUserId);

    Result ListFollowee(String userId, Page<User> page);
}
