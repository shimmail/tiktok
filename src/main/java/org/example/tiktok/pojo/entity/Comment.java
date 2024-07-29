package org.example.tiktok.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("comment")
public class Comment {
    /**
     * 子评论数，子评论的数量
     */
    private long childCount;
    /**
     * 评论文本，建议进行一定的文本处理
     */
    private String content;

    /**
     * 评论 ID，唯一标识符，可选自增/雪花/UUID/其他
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 点赞数，评论点赞的数量
     */
    private long likeCount;
    /**
     * 父评论 ID，父评论的唯一标识符
     */
    private String parentId;

    /**
     * 发表者 ID，发表评论的用户唯一标识符
     */
    private String userId;
    /**
     * 视频 ID，视频的唯一标识符
     */
    private String videoId;

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
}