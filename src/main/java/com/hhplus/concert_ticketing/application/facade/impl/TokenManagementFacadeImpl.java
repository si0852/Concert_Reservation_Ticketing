package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.application.facade.TokenManagementFacade;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
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
    private final Integer maxActiveTokens = 10;

    public TokenManagementFacadeImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    @Override
    public Token insertToken(Long userId) {
        // 토큰 존재여부 확인
        // -> userId 조건으로 토큰 존재여부 확인
        Token token = tokenService.validateToken(userId);
        // -> status: ACTIVE 조건으로 토큰 존재여부 확인
        List<Token> activeToken = tokenService.getTokenListByStatus(TokenStatus.ACTIVE.toString());
        // -> status: WAITING 조건으로 토큰 존재여부 확인
        List<Token> waitingToken = tokenService.getTokenListByStatus(TokenStatus.WAITING.toString());

        Token generatedToken = null;

        // 토큰 발급
        // -> userId로 조회된 토큰이 없을 경우
        if(token == null) generatedToken = tokenService.generateToken(userId);
        // -> userId로 조회된 토큰이 이미 존재 && ACTIVE 상태이면 예약중이므로 익셉션 발생
        if(token != null && (token.getStatus().equals(TokenStatus.ACTIVE.toString()))) throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "예약 진행중인 데이터가 존재합니다.", SeatStatus.RESERVED.toString()));

        // -> userId로 조회된 토큰이 이미 존재 && EXPIRED 상태이면 발급날짜만 발급
        else if (token != null && token.getStatus().equals(TokenStatus.EXPIRED.toString())) generatedToken = tokenService.generateToken(userId, token.getToken());

        // -> userId로 조회된 토큰이 존재하고 Waiting 상태이면 generateToken은 null 값이다.

        // 대기열에는 40개의 토큰만 들어올 수 있다. activeSpace는 대기열의 남은 자리를 의미한다.
        int activeSpace = maxActiveTokens - activeToken.size();
        // 대기열 남은 자리 - Waiting 상태인 토큰 수
        int spaceQueue = activeSpace - waitingToken.size();

        // 대기열에 자리가 남아있다면
        if (activeSpace>0 && spaceQueue>0) {
            generatedToken.setStatus(TokenStatus.ACTIVE.toString());
        }


        return tokenService.saveToken(generatedToken);
    }

    @Transactional
    @Override
    public Integer getTokenPosition(String token) {
        List<Token> waitingToken = tokenService.getTokenListByStatus(TokenStatus.WAITING.toString());

        // index 찾기
        int index = IntStream.range(0, waitingToken.size())
                .filter(data -> Objects.equals(waitingToken.get(data).getToken(), token))
                .findFirst().orElse(-1);

        if(waitingToken.get(index).getStatus().equals(TokenStatus.ACTIVE.toString())) throw new ExistDataInfoException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN, "예약 진행중인 데이터가 존재합니다.", SeatStatus.RESERVED.toString()));
        else if(waitingToken.get(index).getStatus().equals(TokenStatus.EXPIRED.toString())) throw new InvalidTokenException(new ResponseDto(HttpServletResponse.SC_FORBIDDEN,"토큰이 만료되었습니다.", null));
        return index+1;
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
