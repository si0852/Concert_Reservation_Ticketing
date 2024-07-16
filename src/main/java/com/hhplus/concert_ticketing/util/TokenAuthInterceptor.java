package com.hhplus.concert_ticketing.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.status.TokenStatus;
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
        Token getToken = tokenService.validateTokenByToken(token);
        if (getToken != null && getToken.getToken().equals(TokenStatus.ACTIVE.toString()) && getToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            log.error("Error : 토큰이 만료되었습니다.");
            return false;
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
