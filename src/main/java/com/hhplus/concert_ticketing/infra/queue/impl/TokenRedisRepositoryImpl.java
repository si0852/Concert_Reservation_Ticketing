package com.hhplus.concert_ticketing.infra.queue.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.repository.TokenRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class TokenRedisRepositoryImpl implements TokenRedisRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ObjectMapper mapper;

    @Override
    public void saveUser(Token token) {
        Double score = Double.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        redisTemplate.opsForZSet().add("Waiting", toJson(token), score);
        redisTemplate.opsForValue().set(token.getToken(), Duration.ofMinutes(10));
    }

    @Override
    public void deleteUser(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public Token checkUser(Long tokenId) {
//        redisTemplate.opsForValue()
        return null;
    }

    private String toJson(Token token) {
        try {
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(token);
        } catch (Exception e) {
            return null;
        }
    }
}
