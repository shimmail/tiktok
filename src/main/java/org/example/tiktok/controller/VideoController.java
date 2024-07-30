package org.example.tiktok.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.VideoDTO;
import org.example.tiktok.pojo.dto.VideoSearchDTO;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.PageVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.VideoService;
import org.example.tiktok.util.AliOSSUtil;
import org.example.tiktok.util.FileUploadUtils;
import org.example.tiktok.util.JWTUtils;
import org.example.tiktok.util.SearchHistoryUtil;
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
    @Autowired
    private FileUploadUtils fileUploadUtils;
    @Autowired
    private SearchHistoryUtil searchHistoryUtil;

    //视频上传
    @PostMapping("/publish")
    public Result saveVideo(@RequestParam("data") MultipartFile videoUrl,
                          @RequestParam("title") String title,
                          @RequestParam("description") String description,
                          @RequestHeader("Access-Token") String accessToken) throws IOException {
        try {
            String coverUrl = "";
            String userId = JWTUtils.getId(accessToken);

            //上传视频，得到url
            // 本地存储
            String video = fileUploadUtils.uploadFile(videoUrl);

            // 阿里云存储
/*          String video = aliOSSUtil.uploadVideo(videoUrl);
            coverUrl = aliOSSUtil.getVideoCoverlUrl(video);*/

            VideoDTO videoDTO = new VideoDTO(userId , 0,description,0,title,video,coverUrl,0, LocalDateTime.now(),LocalDateTime.now());
            log.info("视频上传：{}", videoDTO);
            return videoService.saveVideo(videoDTO);
        }  catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    //视频点击量排行榜
    @GetMapping("/popular")
    public Result<PageVO<Video>> popular(@RequestParam(name = "page_num", defaultValue = "0") Integer pageNum,
                          @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize){
        pageNum++;
        Page<Video> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        log.info("分页：{}",page);
        return videoService.ListPopular(page);
    }

    //视频搜索
    @PostMapping("/search")
    public Result<PageVO<Video>> search(@RequestParam(name = "page_num", defaultValue = "0") Integer pageNum,
                                        @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
                                        @RequestParam(name = "keywords" ,required = false) String keywords,
                                        @RequestParam(name = "username",required = false) String username,
                                        @RequestParam(name = "from_date",required = false) Integer fromDate,
                                        @RequestParam(name = "to_date",required = false) Integer toDate,
                                        @RequestParam(name = "sort_by",defaultValue = "0") Integer sortBy,
                                        @RequestHeader("Access-Token") String accessToken){
        try {
            pageNum++;
            Page<Video> page = new Page<>(pageNum, pageSize);
            VideoSearchDTO videoSearchDTO = new VideoSearchDTO(keywords,username,fromDate,toDate,sortBy);

            // 将搜索记录存入redis
            String userId = "0";// 默认游客搜索
            if (!accessToken.isEmpty()) {
                userId = JWTUtils.getId(accessToken);
            }
            searchHistoryUtil.saveSearchHistory(userId, videoSearchDTO);
            return videoService.searchVideo(videoSearchDTO,page);

        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 根据 user_id 查看指定人的发布列表
    @GetMapping("/list")
    public Result<PageVO<Video>> listVideoById(@RequestParam(name = "user_id") String userId,
                                               @RequestParam(name = "page_num", defaultValue = "0") Integer pageNum,
                                               @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
                                               @RequestHeader("Access-Token") String accessToken){
        pageNum++;
        Page<Video> page = new Page<>(pageNum, pageSize);
        return videoService.listVideoById(userId,page);
    }

}
