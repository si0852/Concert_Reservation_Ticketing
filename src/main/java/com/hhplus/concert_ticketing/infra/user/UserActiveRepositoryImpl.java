package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.repository.UserActiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Component
public class UserActiveRepositoryImpl implements UserActiveRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final static String activeKey = "Active";

    @Override
    public void saveActiveUser(String userId, Double score) {
        redisTemplate.opsForZSet().add(activeKey, userId, score);
    }

    @Override
    public void expireUser(String userId) {
        redisTemplate.expire(activeKey, Duration.ofMinutes(10));
    }


    @Override
    public Object deleteActiveUser(String userId) {
        return redisTemplate.opsForZSet().remove(activeKey, userId);
    }

    @Override
    public Boolean checkUser(String userId) {
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        Set<Object> range = zSet.range(activeKey, 0, -1);
        return range.contains(userId);
    }

    @Override
    public ZSetOperations<String, Object> countActiveUser() {
        return redisTemplate.opsForZSet();

    }

}
