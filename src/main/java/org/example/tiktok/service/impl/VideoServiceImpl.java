package org.example.tiktok.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.mapper.UserMapper;
import org.example.tiktok.mapper.VideoMapper;
import org.example.tiktok.pojo.dto.VideoDTO;
import org.example.tiktok.pojo.dto.VideoSearchDTO;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.PageVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoMapper videoMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;


    public static final String SCORE_RANK = "score_rank";
    private static final String POPULAR_VIDEOS_CACHE_KEY = "popular_videos";

    // 视频上传
    @Override
    public Result saveVideo(VideoDTO videoDTO) {
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

            PageVO<Video> videoPageVO = new PageVO<>(videos , totalVideos);
            return Result.success(videoPageVO);
        }
    }

    @Override
    public Result<PageVO<Video>> searchVideo(VideoSearchDTO videoSearchDTO, Page<Video> page) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        if (videoSearchDTO.getKeywords() != null && !videoSearchDTO.getKeywords().isEmpty()) {
            //查询条件
            queryWrapper.like("title", videoSearchDTO.getKeywords())
                    .or()
                    .like("description", videoSearchDTO.getKeywords());
        }

        if (videoSearchDTO.getUsername() != null && !videoSearchDTO.getUsername().isEmpty()) {
            // 根据用户名模糊匹配用户ID
            List<User> users = userMapper.selectByUsernameLike(videoSearchDTO.getUsername());
            List<String> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            for (String id : userIds){
                queryWrapper.in("user_id", userIds);
            }
        }

        if (videoSearchDTO.getFromDate() != null) {
            queryWrapper.ge("created_at", videoSearchDTO.getFromDate());
        }

        if (videoSearchDTO.getToDate() != null) {
            queryWrapper.le("created_at", videoSearchDTO.getToDate());
        }

        int sortBy = videoSearchDTO.getSortBy();
        if(sortBy == 1){
            queryWrapper.orderByDesc("created_at"); // 按创建时间降序
        }
        else {
            queryWrapper.orderByDesc("visit_count"); // 按点击量降序
        }

        Page<Video> videoPage = videoMapper.selectPage(page, queryWrapper);
        PageVO<Video> videoPageVO = new PageVO<>(videoPage.getRecords() , videoPage.getTotal());

        return Result.success(videoPageVO);
    }

    // 获得用户视频上传列表
    @Override
    public Result<PageVO<Video>> listVideoById(String id, Page<Video> page) {
        try {
            // 创建查询条件
            QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id);  // 根据用户ID过滤

            // 执行分页查询
            IPage<Video> videoPage = videoMapper.selectPage(page, queryWrapper);

            // 封装结果到 PageVO 对象
            PageVO<Video> pageVO = new PageVO<>(videoPage.getRecords(),videoPage.getTotal());

            // 返回结果
            return Result.success(pageVO);
        } catch (Exception e) {
            return Result.error("查询视频列表失败: " + e.getMessage());
        }
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
