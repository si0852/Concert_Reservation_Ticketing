package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.repository.UserWaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Component
public class UserWaitingRepositoryImpl implements UserWaitingRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final static String waitingKey = "Waiting";


    @Override
    public void saveWaitingUser(String userId) {
        redisTemplate.opsForSet().add(waitingKey, userId);
    }

    @Override
    public Object deleteWaitingUser(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    @Override
    public Boolean checkUser(String userId) {
        SetOperations<String, Object> so = redisTemplate.opsForSet();
        Set<Object> members = so.members(waitingKey);
        return members.contains(userId);
    }

    @Override
    public SetOperations<String, Object> getWaitingUser() {
        return redisTemplate.opsForSet();
    }

}
