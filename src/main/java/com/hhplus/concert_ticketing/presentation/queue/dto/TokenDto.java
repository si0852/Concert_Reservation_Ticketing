package com.hhplus.concert_ticketing.presentation.queue.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String token;
    private int position;
    private LocalDateTime expiredAt;
    private int waitingCount;
}
