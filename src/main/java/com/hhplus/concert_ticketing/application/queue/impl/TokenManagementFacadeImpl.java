package com.hhplus.concert_ticketing.application.queue.impl;

import com.hhplus.concert_ticketing.application.queue.TokenManagementFacade;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.presentation.queue.dto.TokenDto;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.ExistDataInfoException;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Component
public class TokenManagementFacadeImpl implements TokenManagementFacade {

    private final TokenService tokenService;

    public TokenManagementFacadeImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Token insertToken(Long userId) {
        // 토큰 존재여부 확인
        // -> userId 조건으로 토큰 존재여부 확인
        Token token;

        try {
            token = tokenService.validateToken(userId);
        } catch (Exception e) {
            throw e;
        }

        Token generatedToken = null;

        try {
            // 토큰 발급
            // -> userId로 조회된 토큰이 없을 경우
            if (token == null) generatedToken = tokenService.generateToken(userId);
            // -> userId로 조회된 토큰이 이미 존재 && ACTIVE 상태이면 예약중이므로 익셉션 발생
            if (token != null && (token.getStatus().equals(TokenStatus.ACTIVE.toString())))
                throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "예약 진행중인 데이터가 존재합니다.", SeatStatus.RESERVED.toString()));

                // -> userId로 조회된 토큰이 이미 존재 && EXPIRED 상태이면 발급날짜만 발급
            else if (token != null && token.getStatus().equals(TokenStatus.EXPIRED.toString()))
                generatedToken = tokenService.generateToken(userId, token.getToken());

        } catch (Exception e) {
            throw e;
        }

        return generatedToken;
    }

    @Override
    public TokenDto getTokenData(String token) {
        Token tokenInf;
        try {
            tokenInf = tokenService.validateToken(token);
        } catch (Exception e) {
            throw e;
        }
        Integer tokenPosition = tokenService.getTokenPosition(token);
        Integer activeTokenCount = tokenService.getTokenListByStatus(TokenStatus.ACTIVE.toString()).size();
        Integer waitingCount = tokenPosition + activeTokenCount;
        TokenDto tokenDto = new TokenDto(token, tokenPosition, tokenInf.getExpiresAt(), waitingCount);
        return tokenDto;
    }

    @Override
    public Integer getTokenPosition(String token) {
        try {
            List<Token> waitingToken = tokenService.getTokenListByStatus(TokenStatus.WAITING.toString());

            // index 찾기
            int index = IntStream.range(0, waitingToken.size())
                    .filter(data -> Objects.equals(waitingToken.get(data).getToken(), token))
                    .findFirst().orElse(-1);

            if (waitingToken.get(index).getStatus().equals(TokenStatus.ACTIVE.toString()))
                throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "예약 진행중인 데이터가 존재합니다.", SeatStatus.RESERVED.toString()));
            else if (waitingToken.get(index).getStatus().equals(TokenStatus.EXPIRED.toString()))
                throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "토큰이 만료되었습니다.", null));
            return index + 1;
        } catch (Exception e) {
            throw new RuntimeException("Server Error : " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public Token getTokenInfo(String token)  throws Exception{
        Token tokenInfo = tokenService.validateTokenByToken(token);
        if(tokenInfo.getStatus().equals(TokenStatus.EXPIRED.toString())) throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN,"토큰이 만료되었습니다.", null));
        else if(tokenInfo.getStatus().equals(TokenStatus.ACTIVE.toString())) throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "예약 진행중인 데이터가 존재합니다.", SeatStatus.RESERVED.toString()));
        // 토큰 정보 - 롱폴링
        return tokenInfo;
    }

}
