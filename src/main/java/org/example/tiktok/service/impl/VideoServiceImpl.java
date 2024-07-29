package org.example.tiktok.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.mapper.VideoMapper;
import org.example.tiktok.pojo.dto.VideoDTO;
import org.example.tiktok.pojo.dto.VideoSearch;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.PageVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    UserMapper userMapper;


    public static final String SCORE_RANK = "score_rank";
    private static final String POPULAR_VIDEOS_CACHE_KEY = "popular_videos";

    // 视频上传
    @Override
    public Result publish(VideoDTO videoDTO) {
        Video video = new Video();
        BeanUtils.copyProperties(videoDTO, video);
        // 保存视频到数据库
        videoMapper.insert(video);
        // 更新redis缓存
        updateCacheForPopularVideos();
        // 返回结果
        return Result.success();
    }

    // 视频播放量排行榜
    @Override
    public Result ListPopular(Page<Video> page) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String cacheKey = POPULAR_VIDEOS_CACHE_KEY + page.getCurrent();
        updateCacheForPopularVideos();
        // 检查数据是否在缓存中
        if (redisTemplate.hasKey(cacheKey)) {

            // 从缓存中获取数据
            String cachedData = valueOperations.get(cacheKey);
            List<Video> allVideos = JSON.parseArray(cachedData, Video.class);

            // 计算分页起始和结束索引
            int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
            int toIndex = (int) Math.min(fromIndex + page.getSize(), allVideos.size());

            // 获取当前页的数据
            List<Video> videos = allVideos.subList(fromIndex, toIndex);

            // 封装分页结果
            PageVO<Video> videoPageVO = new PageVO<>();
            videoPageVO.setItems(videos);
            videoPageVO.setTotal(allVideos.size());
            return Result.success(videoPageVO);

        } else {
            // 从数据库中获取数据
            QueryWrapper<Video> queryWrapper = new QueryWrapper<Video>()
                    .orderByDesc("visit_count");  // 按访问量降序排序

            Page<Video> videoPage = videoMapper.selectPage(page, queryWrapper);
            List<Video> videos = videoPage.getRecords();

            // 获取视频总数
            int totalVideos = Math.toIntExact(videoMapper.selectCount(null));  // 查询所有视频的总数

            // 缓存结果
            valueOperations.set(cacheKey, JSON.toJSONString(videos), 10, TimeUnit.MINUTES);

            PageVO<Video> videoPageVO = new PageVO<>();
            videoPageVO.setItems(videos);
            videoPageVO.setTotal(totalVideos);
            return Result.success(videoPageVO);
        }
    }

    @Override
    public Result<PageVO<Video>> search(VideoSearch videoSearch, Page<Video> page) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        if (videoSearch.getKeywords() != null && !videoSearch.getKeywords().isEmpty()) {
            queryWrapper.like("title", videoSearch.getKeywords())
                    .or()
                    .like("description", videoSearch.getKeywords());
        }

        if (videoSearch.getUsername() != null && !videoSearch.getUsername().isEmpty()) {
            // 根据用户名模糊匹配用户ID
            List<User> users = userMapper.selectByUsernameLike(videoSearch.getUsername());
            List<String> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            for (String id : userIds){
                queryWrapper.in("user_id", userIds);
            }
        }

        if (videoSearch.getFromDate() != null) {
            queryWrapper.ge("created_at", videoSearch.getFromDate());
        }

        if (videoSearch.getToDate() != null) {
            queryWrapper.le("created_at", videoSearch.getToDate());
        }

        Page<Video> videoPage = videoMapper.selectPage(page, queryWrapper);
        PageVO<Video> videoPageVO = new PageVO<>();
        videoPageVO.setItems(videoPage.getRecords());
        videoPageVO.setTotal(videoPage.getTotal());

        return Result.success(videoPageVO);
    }

    //更新排行榜缓存
    public void updateCacheForPopularVideos() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String cacheKey = POPULAR_VIDEOS_CACHE_KEY + 1;  // 可以根据需要修改页码

        // 从数据库中获取最新数据
        QueryWrapper<Video> queryWrapper = new QueryWrapper<Video>().orderByDesc("visit_count");
        List<Video> videos = videoMapper.selectList(queryWrapper);

        // 更新缓存
        valueOperations.set(cacheKey, JSON.toJSONString(videos), 10, TimeUnit.MINUTES);
    }
}
