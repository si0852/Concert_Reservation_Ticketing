package com.hhplus.concert_ticketing.domain.user.repository;


import org.springframework.data.redis.core.SetOperations;

public interface UserWaitingRepository {

    void saveWaitingUser(String userId);

    Object deleteWaitingUser(String key);

    Boolean checkUser(String userId);

    SetOperations<String, Object> getWaitingUser();

}
