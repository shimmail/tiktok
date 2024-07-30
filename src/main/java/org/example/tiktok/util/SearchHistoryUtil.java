package org.example.tiktok.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tiktok.pojo.dto.VideoSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SearchHistoryUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     *@描述 增加搜索记录
     * @param saveKey 参与搜索的用户id
     * @param videoSearchDTO 视频搜索内容
     */
    public void saveSearchHistory(String saveKey, VideoSearchDTO videoSearchDTO) {
        // 将 VideoSearch 对象序列化为一个 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(videoSearchDTO);
            // 将序列化后的 JSON 字符串添加到 Redis 列表中
            redisTemplate.opsForList().leftPush(saveKey, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveVideoSearchToJson(RedisTemplate<String, String> redisTemplate, VideoSearchDTO videoSearchDTO) {
        // 将 VideoSearch 对象序列化为一个 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(videoSearchDTO);
            redisTemplate.opsForValue().set("videoSearch:" + videoSearchDTO.getUsername() + ":" + videoSearchDTO.getKeywords(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}