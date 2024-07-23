package org.example.tiktok.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.SocialService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
public class SocialController {
    @Autowired
    private SocialService socialService;

    //关注
    @PostMapping("/relation/action")
    public Result followUser(
            @RequestParam(name = "to_user_id") String toUserId,
            @RequestParam(name = "action_type") Integer actionType,
            @RequestHeader("Access-Token") String accessToken) {

        //解析token获得username
        String id = JWTUtils.getId(accessToken);
        if (actionType == 0) {
            try {
                return socialService.follow(id, toUserId);
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        } else if (actionType == 1) {
            try {
                return socialService.unfollow(id, toUserId);
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        } else {
            return Result.error("请求无效");
        }


    }

    //根据 user_id 查看指定人的关注列表
    @GetMapping("/following/list")
    public Result ListFollowee(@RequestParam(name = "user_id", required = true) String userId,
                               @RequestParam(name = "page_num", defaultValue = "0") Integer pageNum,
                               @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize) {
        Page<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        return socialService.ListFollowee(userId,page);
    }
}