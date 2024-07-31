package com.hhplus.concert_ticketing.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.status.TokenStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

@SpringBootTest
public class RedisServiceTest {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceTest.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void data_set_with_redis() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = "first";
        String content = "helloWorld!";
        valueOperations.set(key, content);

        String value = valueOperations.get(key);

        Assertions.assertThat(value).isEqualTo(content);
    }

    @Test
    void data_set_zSet() throws Exception{
        for (int i = 1; i < 15; i++) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            Token token = new Token(1L, "adfasdfs" + i, TokenStatus.ACTIVE.toString(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
            String json = mapper.writeValueAsString(token);
            log.info("JSON : " + json);
            redisTemplate.opsForZSet().add("Active", json, Double.valueOf(LocalDateTime.now().plusSeconds(i).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))));
            redisTemplate.expire("Active", Duration.ofMinutes(5));
        }

        ZSetOperations<String, String> stringStringZSetOperations = redisTemplate.opsForZSet();

        Set<String> active = stringStringZSetOperations.range("Active", 0, 10);
        log.info("active : " + Arrays.toString(active.toArray()));
    }

}


