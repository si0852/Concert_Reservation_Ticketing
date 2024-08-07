package com.hhplus.concert_ticketing.domain.user.service;

import com.hhplus.concert_ticketing.domain.user.repository.UserActiveRepository;
import com.hhplus.concert_ticketing.domain.user.repository.UserWaitingRepository;
import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class UserQueueServiceImpl implements UserQueueService {

    private final UserActiveRepository userActiveRepository;
    private final UserWaitingRepository userWaitingRepository;
    private final static String waitingKey = "Waiting";;
    private final static String activeKey = "Active";

    public UserQueueServiceImpl(UserActiveRepository userActiveRepository, UserWaitingRepository userWaitingRepository) {
        this.userActiveRepository = userActiveRepository;
        this.userWaitingRepository = userWaitingRepository;
    }

    @Transactional
    @Override
    public void addToQueue(String userId) {
       userWaitingRepository.saveWaitingUser(userId);
    }

    @Transactional
    @Override
    public void deleteToWaitingQueue(String key) {
        userWaitingRepository.deleteWaitingUser(key);
    }

    @Override
    public boolean whetherToIncludeQueue(String userId) {
        return userWaitingRepository.checkUser(userId);
    }

    @Transactional
    @Override
    public void convertWaitingToActive(String userId) {
        double score = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        userActiveRepository.saveActiveUser(userId, score);
    }

    @Transactional
    @Override
    public void expiredActiveQueue(String userId) {
        userActiveRepository.expireUser(userId);
    }

    @Transactional
    @Override
    public void deleteToActiveQueue(String userId) {
        Object o = userActiveRepository.deleteActiveUser(userId);
        if(o == null) throw new RuntimeException();
    }

    @Transactional
    @Override
    public boolean checkActiveUser(String userId) {
        return userActiveRepository.checkUser(userId);
    }

    @Transactional
    @Override
    public Integer countActiveUserSize() {
        return userActiveRepository.countActiveUser().range(activeKey,0, -1).size();
    }

    @Override
    public ZSetOperations<String, Object> countActiveUser() {
        return userActiveRepository.countActiveUser();
    }

    @Override
    public SetOperations<String, Object> countWaitingUser() {
        return userWaitingRepository.getWaitingUser();
    }

    @Override
    public Long countWaitingUserSize() {
        return userWaitingRepository.getWaitingUser().size(waitingKey);
    }

}
