package org.example.tiktok.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("social")
public class Social {
    @TableId
    private String id;
    /**
     *粉丝的id/本人id
     */
    private String followerId;
    /**
     * 关注的id
     */
    private String followeeId;

    private java.util.Date createdAt;
}
