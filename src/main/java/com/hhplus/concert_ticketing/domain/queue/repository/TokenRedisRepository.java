package com.hhplus.concert_ticketing.domain.queue.repository;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;

import java.util.List;

public interface TokenRedisRepository {

    void saveUser(Token token);

    void deleteUser(String token);

    Token checkUser(Long tokenId);

}
