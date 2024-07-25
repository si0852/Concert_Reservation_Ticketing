package com.hhplus.concert_ticketing.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TokenAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    private final TokenService tokenService;

    public TokenAuthInterceptor(ObjectMapper objectMapper, TokenService tokenService) {
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            log.error("잘못된 요청입니다.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }else {
            try {
                Token getToken = tokenService.validateTokenByToken(token);
                if (getToken == null) {
                    log.error("토큰 정보가 없습니다.");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                } else {
                    if (!getToken.getToken().equals(TokenStatus.ACTIVE.toString())) {
                        log.error("접근할수 없는 토큰 입니다.");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    }
                }
            } catch (Exception e) {
                log.error("Error : " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        final ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;
        log.info(
                "\n HTTP Method : {} " +
                        "\n Request URI : {} " +
                        "\n AccessToken Exist : {} " +
                        "\n Request Body : {}",
                request.getMethod(),
                request.getRequestURI(),
                StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION)),
                objectMapper.readTree(cachingRequest.getContentAsByteArray())
        );

        log.info(
                "\n Response Body: {}", objectMapper.readTree(cachingResponse.getContentAsByteArray())
        );
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
