package com.hhplus.concert_ticketing.domain.queue.service.impl;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.repository.TokenRepository;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Token generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        String status = TokenStatus.WAITING.toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now;
        LocalDateTime expiresAt = now.plusHours(1);
        return new Token(userId, token, status, createdAt, expiresAt);
    }

    @Override
    public Token generateToken(Long userId, String token) {
        String status = TokenStatus.WAITING.toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now;
        LocalDateTime expiresAt = now.plusHours(1);
        return new Token(userId, token, status, createdAt, expiresAt);
    }

    @Override
    public Token saveToken(Token token) {
        return tokenRepository.saveToken(token);
    }

    @Override
    public Token validateToken(Long userId) {
        Token token = tokenRepository.getToken(userId);
        return token;
    }

    @Override
    public Token validateTokenByTokenId(Long tokenId) {
        Token token = tokenRepository.getTokenByTokenId(tokenId);
        if(token == null) throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN,"토큰 정보가 없습니다.", null));
        return token;
    }

    @Override
    public Token validateTokenByToken(String token) throws Exception{
        Token tokenByToken = tokenRepository.getTokenByToken(token);
//        if(tokenByToken == null) throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN,"토큰 정보가 없습니다.", null));
//        if(tokenByToken != null && !tokenByToken.getStatus().equals(TokenStatus.ACTIVE.toString())) throw new RuntimeException("유효한 토큰이 아닙니다.");
        return tokenByToken;
    }

    @Override
    public Token updateToken(Token token) {
        return tokenRepository.updateToken(token);
    }

    @Override
    public List<Token> getTokenListByStatus(String status) {
        List<Token> tokenListByStatus = tokenRepository.getTokenListByStatus(status);
        return tokenListByStatus;
    }
}
