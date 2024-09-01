package org.example.tiktok.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class User {
    /**
     * 用户 ID，用户唯一标识符
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
     * 用户权限
     */
    private String role;

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
