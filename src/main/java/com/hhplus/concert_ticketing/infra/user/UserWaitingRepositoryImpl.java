package com.hhplus.concert_ticketing.infra.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.domain.queue.repository.UserWaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;


public class UserWaitingRepositoryImpl implements UserWaitingRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final static String waitingKey = "Waiting";

    @Autowired
    ObjectMapper mapper;

    @Override
    public void saveWaitingUser(Long userId) {
        redisTemplate.opsForSet().add(waitingKey, userId);
    }

    @Override
    public Object deleteWaitingUser(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    @Override
    public Boolean checkUser(Long userId) {
        SetOperations<String, Object> so = redisTemplate.opsForSet();
        Set<Object> members = so.members(waitingKey);
        return members.contains(userId);
    }

}
