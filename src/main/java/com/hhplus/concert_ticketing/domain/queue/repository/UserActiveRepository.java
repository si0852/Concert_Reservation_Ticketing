package com.hhplus.concert_ticketing.domain.queue.repository;


public interface UserActiveRepository {

    void saveActiveUser(Long userId);

    Object deleteActiveUser(Long userId);

    Boolean checkUser(Long userId);

}
