package org.example.tiktok.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.tiktok.pojo.entity.User;
import org.example.tiktok.pojo.vo.UserVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
@Component
public interface UserMapper extends BaseMapper<User> {
    void updateAvatarUrlByID(@Param("id") String id, @Param("avatarUrl") String avatarUrl);

    UserVO selectByUsername(String username);
}
