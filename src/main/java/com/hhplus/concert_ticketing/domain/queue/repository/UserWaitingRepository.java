package com.hhplus.concert_ticketing.domain.queue.repository;


public interface UserWaitingRepository {

    void saveWaitingUser(Long userId);

    Object deleteWaitingUser(String key);

    Boolean checkUser(Long userId);

}
