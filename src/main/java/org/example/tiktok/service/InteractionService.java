package org.example.tiktok.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.CommentVO;
import org.example.tiktok.result.Result;

public interface InteractionService {
    Result VideoComment(CommentDTO commentDTO);

    Result CommentReply(CommentDTO commentDTO);


    Result listVideoComment(String videoId, Page page);

    Result listCommentReply(String commentId, Page page);

    Result removeVideoComment(String videoId);

    Result removeCommentReply(String commentId);

    Result likeVideo(String videoId) throws Exception;

    Result dislikeVideo(String videoId) throws Exception;

    Result likeComment(String commentId) throws Exception;

    Result dislikeComment(String commentId) throws Exception;
}
