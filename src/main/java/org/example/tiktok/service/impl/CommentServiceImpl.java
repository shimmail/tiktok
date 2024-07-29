package org.example.tiktok.service.impl;

import org.example.tiktok.exception.CommentException;
import org.example.tiktok.mapper.CommentMapper;
import org.example.tiktok.mapper.VideoMapper;
import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.pojo.entity.Comment;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    CommentMapper commentMapper;

    @Override
    public Result VideoComment(CommentDTO commentDTO) {
        Video video = videoMapper.selectById(commentDTO.getVideoId());
        if(video == null){
            throw new CommentException("视频不存在");
        }
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        commentMapper.insert(comment);
        return Result.success();
    }

    @Override
    public Result CommentReply(CommentDTO commentDTO) {
        Comment comment = commentMapper.selectById(commentDTO.getParentId());
        if(comment == null){
            throw new CommentException("评论不存在");
        }
        Comment newComment = new Comment();
        BeanUtils.copyProperties(commentDTO, newComment);
        commentMapper.insert(newComment);
        return Result.success();
    }
}
