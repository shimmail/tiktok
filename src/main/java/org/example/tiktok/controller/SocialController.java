package org.example.tiktok.controller;

import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.SocialService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SocialController {
    @Autowired
    private SocialService socialService;

    //关注
    @PostMapping("/relation/action")
    public Result followUser(
            @RequestParam(name = "to_user_id") String toUserId,
            @RequestParam(name = "action_type")  Integer actionType,
            @RequestHeader("Access-Token") String accessToken) {

        //解析token获得username
        String id = JWTUtils.getId(accessToken);
        if(actionType == 0) {
            try {
                return socialService.follow(id , toUserId);
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        } else if (actionType == 1) {
            try {
                return socialService.unfollow(id , toUserId);
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        }
        else {
            return Result.error("请求无效");
        }


    }

    }
