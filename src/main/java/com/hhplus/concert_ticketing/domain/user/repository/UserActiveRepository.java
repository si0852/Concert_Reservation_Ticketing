package com.hhplus.concert_ticketing.domain.user.repository;


import org.springframework.data.redis.core.ZSetOperations;

public interface UserActiveRepository {

    void saveActiveUser(String userId, Double score);

    void expireUser(String userId);

    Object deleteActiveUser(String userId);

    Boolean checkUser(String userId);

    ZSetOperations<String, Object> countActiveUser();

}
