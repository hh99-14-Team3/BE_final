package com.mogakko.be_final.redis.util;

import com.mogakko.be_final.domain.friendship.entity.RejectedFriendship;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;
    private final RedisTemplate<String, Object> redisFriendshipTemplate;

    public void set(String key, String val, long time) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(val.getClass()));
        redisTemplate.opsForValue().set(key, val, time, TimeUnit.MINUTES);
    }

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public void delValues(String token) {
        redisTemplate.delete(token);
    }

    public void setBlackList(String key, String str, long minutes) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(str.getClass()));
        redisBlackListTemplate.opsForValue().set(key, str, minutes, TimeUnit.MINUTES);
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }

    public void setRejectedFriendshipWithExpireTime(String key, Object value, long timeout, TimeUnit unit) {
        redisFriendshipTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RejectedFriendship.class));
        redisFriendshipTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public boolean hasKeyFriendship(String key) {
        return Boolean.TRUE.equals(redisFriendshipTemplate.hasKey(key));
    }

    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }
}
