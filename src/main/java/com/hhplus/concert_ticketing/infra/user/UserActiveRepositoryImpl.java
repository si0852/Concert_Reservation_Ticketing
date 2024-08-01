package com.hhplus.concert_ticketing.infra.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.domain.queue.repository.UserActiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;


public class UserActiveRepositoryImpl implements UserActiveRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final static String activeKey = "Active";

    @Autowired
    ObjectMapper mapper;

    @Override
    public void saveActiveUser(Long userId) {
        Double score = Double.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        redisTemplate.opsForZSet().add(activeKey, userId, score);
        redisTemplate.expire(activeKey, Duration.ofMinutes(10));
    }

    @Override
    public Object deleteActiveUser(Long userId) {
        return redisTemplate.opsForZSet().remove(activeKey, userId);
    }

    @Override
    public Boolean checkUser(Long userId) {
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        Set<Object> range = zSet.range(activeKey, 0, -1);
        return range.contains(userId);
    }

}
