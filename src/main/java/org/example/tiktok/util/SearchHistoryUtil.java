package org.example.tiktok.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tiktok.pojo.dto.VideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class SearchHistoryUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     *@描述 增加搜索记录
     * @param saveKey 参与搜索的用户id
     * @param videoSearch 视频搜索内容
     */
    public void saveSearchHistory(String saveKey, VideoSearch videoSearch) {
        // 将 VideoSearch 对象序列化为一个 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(videoSearch);
            // 将序列化后的 JSON 字符串添加到 Redis 列表中
            redisTemplate.opsForList().leftPush(saveKey, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveVideoSearchToJson(RedisTemplate<String, String> redisTemplate, VideoSearch videoSearch) {
        // 将 VideoSearch 对象序列化为一个 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(videoSearch);
            redisTemplate.opsForValue().set("videoSearch:" + videoSearch.getUsername() + ":" + videoSearch.getKeywords(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}