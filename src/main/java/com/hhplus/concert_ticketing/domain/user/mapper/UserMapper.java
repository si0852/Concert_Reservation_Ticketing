package com.hhplus.concert_ticketing.domain.user.mapper;

import com.hhplus.concert_ticketing.presentation.queue.dto.ScoreDto;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public class UserMapper {

    public static ScoreDto toDto(ZSetOperations.TypedTuple<Object> tuple) {
        ScoreDto scoreDto = new ScoreDto();
        String value = String.valueOf(tuple.getValue());
        scoreDto.setUserId(value);
        scoreDto.setScore(tuple.getScore());
        return scoreDto;
    }
}
