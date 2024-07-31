package org.example.tiktok.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.pojo.vo.CommentVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.InteractionService;
import org.example.tiktok.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            CommentDTO commentDTO = new CommentDTO(commentId, content, userId, videoId);

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
                return interactionService.listVideoComment(videoId, page);
            } else if (videoId == null || videoId.isEmpty() && commentId != null && !commentId.isEmpty()) {
                // 评论回复
                return interactionService.listCommentReply(commentId, page);
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
                                @RequestHeader("Access-Token") String accessToken) {
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

    /**
     * 点赞或取消点赞
     *
     * @param videoId     视频ID，可选
     * @param commentId   评论ID，可选
     * @param actionType  操作类型，1为点赞，2为取消，默认1
     * @param accessToken 用户访问令牌
     * @return 操作结果
     */
    @PostMapping("/like/action")
    public Result like(@RequestParam(name = "video_id", required = false) String videoId,
                       @RequestParam(name = "comment_id", required = false) String commentId,
                       @RequestParam(name = "action_type", defaultValue = "1") String actionType,//1点赞，2取消
                       @RequestHeader("Access-Token") String accessToken) {
        try {
            if (videoId != null && !videoId.isEmpty() && (commentId == null || commentId.isEmpty())) {
                if (actionType.equals("1")) {
                    // 视频点赞
                    return interactionService.likeVideo(videoId);
                } else if (actionType.equals("2")) {
                    // 取消点赞
                    return interactionService.dislikeVideo(videoId);
                } else {
                    throw new Exception("参数错误");
                }
            } else if (videoId == null || videoId.isEmpty() && commentId != null && !commentId.isEmpty()) {
                if (actionType.equals("1")) {
                    // 评论点赞
                    return interactionService.likeComment(commentId);
                } else if (actionType.equals("2")) {
                    //取消点赞
                    return interactionService.dislikeComment(commentId);
                } else {
                    throw new Exception("参数错误");
                }
            } else {
                throw new Exception("参数错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
