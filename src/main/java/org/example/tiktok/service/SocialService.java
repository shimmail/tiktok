package org.example.tiktok.service;

import org.example.tiktok.result.Result;

public interface SocialService {
    Result follow(String id, String toUserId);

    Result unfollow(String id, String toUserId);
}
