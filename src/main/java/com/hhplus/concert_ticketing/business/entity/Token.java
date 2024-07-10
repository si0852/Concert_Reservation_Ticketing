package com.hhplus.concert_ticketing.business.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Token {
    @Id
    Long tokenId;
    @NotNull
    Long userId;
    String token;
    String status; // waiting, working, expired
    LocalDateTime createdAt;
    LocalDateTime expiresAt;

    public Token(Long user_id, String token, String status, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.userId = user_id;
        this.token = token;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
