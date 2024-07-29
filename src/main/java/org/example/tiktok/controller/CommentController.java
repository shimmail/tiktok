package org.example.tiktok.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.CommentService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("comment")
public class CommentController {

    @Autowired
    CommentService commentService;
    @PostMapping("/publish")
    public Result publish(@RequestParam(name = "video_id", required = false) String videoId,
                          @RequestParam(name = "comment_id", required = false) String commentId,
                          @RequestParam(name = "content", required = true) String content,
                          @RequestHeader("Access-Token") String accessToken) throws Exception {
        try {
            String userId = JWTUtils.getId(accessToken);
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setUserId(userId);
            commentDTO.setVideoId(videoId);
            commentDTO.setParentId(commentId);
            commentDTO.setContent(content);
            commentDTO.setCreatedAt(LocalDateTime.now());
            commentDTO.setUpdatedAt(LocalDateTime.now());
            log.info("评论：{}",commentDTO);

            if (videoId != null && !videoId.isEmpty() && (commentId == null || commentId.isEmpty())) {
                // 视频评论
                return commentService.VideoComment(commentDTO);
            } else if (videoId == null || videoId.isEmpty() && commentId != null && !commentId.isEmpty()) {
                // 评论回复
                return commentService.CommentReply(commentDTO);
            } else {
                throw new Exception("参数错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }

    }
}
