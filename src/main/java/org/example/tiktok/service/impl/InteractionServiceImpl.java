package org.example.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tiktok.exception.CommentException;
import org.example.tiktok.mapper.CommentMapper;
import org.example.tiktok.mapper.VideoMapper;
import org.example.tiktok.pojo.dto.CommentDTO;
import org.example.tiktok.pojo.entity.Comment;
import org.example.tiktok.pojo.entity.Video;
import org.example.tiktok.pojo.vo.CommentVO;
import org.example.tiktok.pojo.vo.PageVO;
import org.example.tiktok.result.Result;
import org.example.tiktok.service.InteractionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InteractionServiceImpl implements InteractionService {
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    CommentMapper commentMapper;

    //视频评论
    @Override
    public Result VideoComment(CommentDTO commentDTO) {
        Video video = videoMapper.selectById(commentDTO.getVideoId());
        if (video == null) {
            throw new CommentException("视频不存在");
        }

        // 更新视频的评论数量
        video.setCommentCount(video.getCommentCount() + 1);
        videoMapper.updateById(video);

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        commentMapper.insert(comment);
        return Result.success();
    }

    //评论回复
    @Override
    public Result CommentReply(CommentDTO commentDTO) {
        Comment parentComment = commentMapper.selectById(commentDTO.getParentId());
        if (parentComment == null) {
            throw new CommentException("评论不存在");
        }

        commentDTO.setVideoId(parentComment.getVideoId());
        // 更新父评论
        parentComment.setChildCount(parentComment.getChildCount() + 1);
        parentComment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(parentComment);

        // 更新视频的评论数量
        Video video = videoMapper.selectById(commentDTO.getVideoId());
        video.setCommentCount(video.getCommentCount() + 1);
        videoMapper.updateById(video);

        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        commentMapper.insert(comment);
        return Result.success();
    }

    //视频评论列表
    @Override
    public Result listVideoComment(String videoId, Page page) {
        // 创建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        return getPageVOResult(page, queryWrapper);
    }


    //评论回复列表
    @Override
    public Result listCommentReply(String commentId, Page page) {
        // 创建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", commentId);
        return getPageVOResult(page, queryWrapper);
    }

    // 删除视频评论
    @Override
    public Result removeVideoComment(String videoId) {
        // 查找视频的所有评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        List<Comment> comments = commentMapper.selectList(queryWrapper);

        if (comments.isEmpty()) {
            throw new RuntimeException("视频不存在或没有评论");
        }
        for (Comment comment : comments) {
            // 递归删除评论及其子评论
            removeCommentReply(comment.getId());
        }

        return Result.success();
    }

    // 递归删除评论及其所有子评论
    @Override
    public Result removeCommentReply(String commentId) {
        // 查找评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 查找所有子评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", commentId);
        List<Comment> replies = commentMapper.selectList(queryWrapper);

        // 递归删除子评论
        for (Comment reply : replies) {
            removeCommentReply(reply.getId());
        }

        // 删除当前评论
        int rows = commentMapper.deleteById(commentId);
        if (rows > 0) {
            // 如果有父评论，则其子评论数减一，更新时间
            if (comment.getParentId() != null) {
                Comment parentComment = commentMapper.selectById(comment.getParentId());
                if (parentComment != null) {
                    parentComment.setChildCount(parentComment.getChildCount() - 1);
                    parentComment.setUpdatedAt(LocalDateTime.now());
                    commentMapper.updateById(parentComment);
                }
            }
            // 更新视频的评论数
            Video video = videoMapper.selectById(comment.getVideoId());
            if (video != null) {
                video.setCommentCount(video.getCommentCount() - 1);
                videoMapper.updateById(video);
            }

            return Result.success();
        }
        return Result.error("评论删除失败");
    }
        // 执行分页查询
        private Result<PageVO<CommentVO>> getPageVOResult (Page page, QueryWrapper < Comment > queryWrapper){
            // 执行分页查询
            IPage<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

            // 转换为 CommentVO 类型的列表
            List<CommentVO> commentVOList = commentPage.getRecords().stream()
                    .map(comment -> {
                        CommentVO commentVO = new CommentVO();
                        BeanUtils.copyProperties(comment, commentVO);
                        // 设置其他必要的属性，例如用户信息等
                        return commentVO;
                    }).collect(Collectors.toList());

            // 封装结果到 PageVO 对象
            PageVO<CommentVO> pageVO = new PageVO<>(commentVOList, commentPage.getTotal());

            // 返回结果
            return Result.success(pageVO);
        }
    }
