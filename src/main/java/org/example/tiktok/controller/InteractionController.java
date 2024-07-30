package org.example.tiktok.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.CommentVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.InteractionService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class InteractionController {

    @Autowired
    InteractionService interactionService;

    @PostMapping("/comment/publish")
    public Result saveComment(@RequestParam(name = "video_id", required = false) String videoId,
                                 @RequestParam(name = "comment_id", required = false) String commentId,
                                 @RequestParam(name = "content", required = true) String content,
                                 @RequestHeader("Access-Token") String accessToken) throws Exception {
        try {
            String userId = JWTUtils.getId(accessToken);
            CommentDTO commentDTO = new CommentDTO(commentId,content,userId,videoId);

            log.info("评论：{}", commentDTO);

            if (videoId != null && !videoId.isEmpty() && (commentId == null || commentId.isEmpty())) {
                // 视频评论
                commentDTO.setParentId(commentDTO.getId());
                return interactionService.VideoComment(commentDTO);
            } else if (videoId == null || videoId.isEmpty() && commentId != null && !commentId.isEmpty()) {
                // 评论回复
                return interactionService.CommentReply(commentDTO);
            } else {
                throw new Exception("参数错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/comment/list")
    public Result listComment(@RequestParam(name = "video_id", required = false) String videoId,
                              @RequestParam(name = "comment_id", required = false) String commentId,
                              @RequestParam(name = "page_num", defaultValue = "0") Integer pageNum,
                              @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize) throws Exception {
        try {
            pageNum++;
            Page<CommentVO> page = new Page<>(pageNum, pageSize);

            if (videoId != null && !videoId.isEmpty() && (commentId == null || commentId.isEmpty())) {
                // 视频评论
                return interactionService.listVideoComment(videoId,page);
            } else if (videoId == null || videoId.isEmpty() && commentId != null && !commentId.isEmpty()) {
                // 评论回复
                return interactionService.listCommentReply(commentId,page);
            } else {
                throw new Exception("参数错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    @DeleteMapping("/comment/delete")
    public Result removeComment(@RequestParam(name = "video_id", required = false) String videoId,
                              @RequestParam(name = "comment_id", required = false) String commentId,
                              @RequestHeader("Access-Token") String accessToken){
        try {
            if (videoId != null && !videoId.isEmpty() && (commentId == null || commentId.isEmpty())) {
                // 视频评论
                return interactionService.removeVideoComment(videoId);
            } else if (videoId == null || videoId.isEmpty() && commentId != null && !commentId.isEmpty()) {
                // 评论回复
                return interactionService.removeCommentReply(commentId);
            } else {
                throw new Exception("参数错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}
