package com.hhplus.concert_ticketing.domain.user.service.impl;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface UserQueueService {

    void addToQueue(String userId);

    void deleteToWaitingQueue(String key);

    boolean whetherToIncludeQueue(String userId);

    void convertWaitingToActive(String userId);

    void expiredActiveQueue(String userId);

    void deleteToActiveQueue(String userId);

    boolean checkActiveUser(String userId);

    Integer countActiveUserSize();

    ZSetOperations<String, Object> countActiveUser();

    SetOperations<String, Object> countWaitingUser();

    Long countWaitingUserSize();

}
