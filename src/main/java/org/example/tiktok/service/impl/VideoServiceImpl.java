package org.example.tiktok.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.mapper.VideoMapper;
import org.example.tiktok.pojo.dto.VideoDTO;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    VideoMapper videoMapper;

    @Override
    public Result publish(VideoDTO videoDTO) {
        Video video = new Video();
        BeanUtils.copyProperties(videoDTO, video);
        // 保存视频到数据库
        videoMapper.insert(video);
        // 返回结果
        return Result.success();
    }

    @Override
    public Result ListPopular(Page<Video> page) {
        return Result.success();
    }
}
