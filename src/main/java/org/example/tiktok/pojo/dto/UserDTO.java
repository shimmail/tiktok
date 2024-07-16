package org.example.tiktok.pojo.dto;

import java.time.LocalDateTime;

/**
 * 用户
 */
@lombok.Data
public class UserDTO {
    /**
     * 用户 ID，用户唯一标识符
     * ID的生成请使用雪花算法等（MybatisPlus有对应的ID生成策略）
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，需要进行加密（bcrypt）
     */
    private String password;

    /**
     * 头像，图片链接，可以是本地也可以是云存储
     */
    private String avatarUrl;

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
