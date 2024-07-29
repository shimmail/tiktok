package org.example.tiktok.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

/**
 * 视频
 */
@lombok.Data
public class VideoDTO {
    /**
     * 视频id，唯一标识符，可选自增/雪花/UUID/其他
     */
    private String id;

    /**
     * 视频作者，发表视频的用户唯一标识符
     */
    private String userId;

    /**
     * 评论数，评论数量
     */
    private long commentCount;

    /**
     * 视频描述，视频描述
     */
    private String description;

    /**
     * 点赞数，点赞数量
     */
    private long likeCount;
    /**
     * 视频标题，视频标题
     */
    private String title;

    /**
     * 视频，视频文件链接
     */
    private String videoUrl;

    /**
     * 封面，封面链接
     */
    private String coverUrl;

    /**
     * 访问量，视频访问量
     */
    private long visitCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;

    public VideoDTO(String userId, long commentCount, String description, long likeCount, String title, String videoUrl, String coverUrl, long visitCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.commentCount = commentCount;
        this.description = description;
        this.likeCount = likeCount;
        this.title = title;
        this.videoUrl = videoUrl;
        this.coverUrl = coverUrl;
        this.visitCount = visitCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}