package com.hhplus.concert_ticketing.domain.queue.service.impl;

import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.repository.TokenRepository;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final Integer maxActiveTokens = 40;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Override
    public Token validateToken(Long userId) {
        Token token = tokenRepository.getToken(userId);
        return token;
    }

    @Transactional
    @Override
    public Token validateToken(String token) {
        Token tokenByToken = tokenRepository.getTokenByToken(token);
        return tokenByToken;
    }


    @Transactional
    @Override
    public Token generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        String status = setTokenStatus().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now;
        LocalDateTime expiresAt = now.plusHours(1);
        Token saveToken = tokenRepository.saveToken(new Token(userId, token, status, createdAt, expiresAt));
        return saveToken;
    }

    @Transactional
    @Override
    public Token generateToken(Long userId, String token) {
        String status = setTokenStatus().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now;
        LocalDateTime expiresAt = now.plusHours(1);
        Token saveToken = tokenRepository.saveToken(new Token(userId, token, status, createdAt, expiresAt));
        return saveToken;
    }

    private TokenStatus setTokenStatus() {
        Integer activeTokenCount = getTokenListByStatus(TokenStatus.ACTIVE.toString()).size();
        if(activeTokenCount < maxActiveTokens) return TokenStatus.ACTIVE;
        return TokenStatus.WAITING;
    }

    @Transactional
    @Override
    public Integer getTokenPosition(String token) {
        List<Token> waitingToken = tokenRepository.getTokenListByStatus(TokenStatus.WAITING.toString());
        int index = IntStream.range(0, waitingToken.size())
                .filter(data -> Objects.equals(waitingToken.get(data).getToken(), token))
                .findFirst().orElse(-1);
        return index+1;
    }


    @Override
    public Token saveToken(Token token) {
        return tokenRepository.saveToken(token);
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
