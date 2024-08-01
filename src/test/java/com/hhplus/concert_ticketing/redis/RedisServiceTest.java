package com.hhplus.concert_ticketing.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hhplus.concert_ticketing.application.queue.TokenManagementFacade;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.user.mapper.UserMapper;
import com.hhplus.concert_ticketing.domain.user.repository.UserWaitingRepository;
import com.hhplus.concert_ticketing.domain.user.service.impl.UserQueueService;
import com.hhplus.concert_ticketing.presentation.queue.dto.ScoreDto;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
public class RedisServiceTest {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceTest.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserWaitingRepository userWaitingRepository;

    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private TokenManagementFacade tokenManagementFacade;


    @Test
    void data_set_with_redis0() {
        SetOperations<String, Object> valueOperations = redisTemplate.opsForSet();
        String key = "Waiting";

        for (int i = 0; i < 10; i++) {
            String content = "helloWorld" + i +"!";
            valueOperations.add(key, content);
        }
//
//        SetOperations<String, Object> so = redisTemplate.opsForSet();
//        Set<Object> first = so.members(key);
//        log.info("Waiting : " + Arrays.toString(first.toArray()));
//        boolean contains = first.contains("helloWorld7!");
//        log.info("contains : " + contains);
//        Object popContent = so.pop(key);
//        log.info("popContent : " + popContent);
    }

    @Test
    void data_set_with_redis() {
        Long userId = 3L;
        String value = String.valueOf(userId);
       userWaitingRepository.saveWaitingUser(value);

        Assertions.assertThat(userWaitingRepository.checkUser(value)).isTrue();
    }

    @Test
    void data_set_with_redis2() {
        for (int i = 1; i < 6; i++) {
            String value = String.valueOf(i);
            tokenManagementFacade.addToQueue(value);

        }

    }

    @Test
    void data_set_with_redis3() {
        Long userId = 6L;
        String value = String.valueOf(userId);
        userQueueService.convertWaitingToActive(value);

        Assertions.assertThat(userQueueService.checkActiveUser(value)).isTrue();
    }


}


