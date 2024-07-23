package org.example.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.tiktok.pojo.entity.Social;
import org.example.tiktok.pojo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SocialMapper extends BaseMapper<Social> {
    void unfollow(String id, String toUserId);

    //用户的关注列表
    @Select("SELECT * FROM social WHERE follower_id = #{userId}")
    List<Social> selectFollowingByUserId(String userId);

    //用户的粉丝列表
    @Select("SELECT * FROM social WHERE followee_id = #{userId}")
    List<Social> selectFollowersByUserId(String userId);
}
