package org.example.tiktok.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.mapper.VideoMapper;
import org.example.tiktok.pojo.dto.VideoDTO;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.VideoService;
import org.example.tiktok.util.AliOSSUtil;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private AliOSSUtil aliOSSUtil;

    //视频上传
    @PostMapping("/publish")
    public Result publish(@RequestParam("data") MultipartFile videoUrl,
                          @RequestParam("title") String title,
                          @RequestParam("description") String description,
                          @RequestHeader("Access-Token") String accessToken) throws IOException {
        try {
            String userId = JWTUtils.getId(accessToken);
            //上传视频，得到url
            String video = aliOSSUtil.uploadVideo(videoUrl);
            //得到视频封面
            String coverUrl = aliOSSUtil.getVideoCoverlUrl(video);
            VideoDTO videoDTO = new VideoDTO(userId , 0,description,0,title,video,coverUrl,0, LocalDateTime.now(),LocalDateTime.now());
            log.info("视频上传：{}", videoDTO);
            return videoService.publish(videoDTO);
        }  catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    //视频点击量排行榜
    @GetMapping("/popular")
    public Result popular(@RequestParam(name = "page_num", defaultValue = "0") Integer pageNum,
                          @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize){
        pageNum++;
        Page<Video> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return videoService.ListPopular(page);

    }
}
