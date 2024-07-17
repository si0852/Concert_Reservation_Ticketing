package com.hhplus.concert_ticketing.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.application.facade.*;
import com.hhplus.concert_ticketing.application.facade.impl.TokenManagementFacadeImpl;
import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.business.service.impl.TokenServiceImpl;
import com.hhplus.concert_ticketing.presentation.dto.request.BalanceRequest;
import com.hhplus.concert_ticketing.presentation.dto.response.BalanceResponse;
import com.hhplus.concert_ticketing.presentation.dto.response.ConcertDateResponse;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.presentation.dto.response.TokenResponse;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.TokenAuthInterceptor;
import com.hhplus.concert_ticketing.util.exception.BadRequestException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest
class TicketingControllerTest {
    private static final Logger log = LoggerFactory.getLogger(TicketingControllerTest.class);

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;


    @MockBean
    TokenAuthInterceptor tokenAuthInterceptor;
    @MockBean
    TokenManagementFacade tokenManagementFacade;
    @MockBean
    ChargeManagementFacade chargeManagementFacade;
    @MockBean
    PaymentManagementFacade paymentManagementFacade;
    @MockBean
    ReservationManagementFacade reservationManagementFacade;
    @MockBean
    StatusManagementFacade statusManagementFacade;
    @MockBean
    ConcertInfoManagementFacade concertInfoManagementFacade;

    @InjectMocks
    TicketingController controller;

    @DisplayName("유저 토큰 발급 API")
    @Test
    void 유저_토큰_발급() throws Exception {
        Long userId = 1L;
        int value = 1;
        String tokenStr = "sdfasdfsadf";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(1);

        Token token = new Token(userId, tokenStr, TokenStatus.WAITING.toString(), LocalDateTime.now(), expiresAt);
        given(tokenManagementFacade.insertToken(userId)).willReturn(token);
        given(tokenManagementFacade.getTokenPosition(tokenStr)).willReturn(value);

        TokenResponse response = TokenResponse.builder()
                .token(token.getToken())
                .queuePosition(1)
                .expired_at(expiresAt)
                .build();

        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", response);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/token")
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("대기열 토큰 순번 APi")
    @Test
    void 토큰_순번() throws Exception {
        String token = "asleisl293sl";
        Integer tokenPosition = 1;

        // Mocking the response from facade
        given(tokenManagementFacade.getTokenPosition(token)).willReturn(tokenPosition);

        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", tokenPosition);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        // when
        MockHttpServletRequestBuilder requestBuilder = get("/api/tokenNum")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("유저 토큰 정보 API")
    @Test
    void 유저_토큰_정보() throws Exception{
        String token = "asleisl293sl";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(1);
        Token tokenInfo = new Token(1L, token, TokenStatus.WAITING.toString(), LocalDateTime.now(), expiresAt);
        Integer tokenPosition = 1;

        // Mocking the response from facade
        given(tokenManagementFacade.getTokenInfo(token)).willReturn(tokenInfo);
        given(tokenManagementFacade.getTokenPosition(token)).willReturn(tokenPosition);

        TokenResponse res = new TokenResponse(tokenInfo.getToken(), tokenPosition, tokenInfo.getExpiresAt());
        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", res);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        // when
        MockHttpServletRequestBuilder requestBuilder = get("/api/tokenInfo")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("예약 가능 날짜 조회")
    @Test
    void 예약_가능_날짜_조회() throws Exception{
        String token = "asleisl293sl";
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Mocking the response from facade
        List<ConcertOption> concertOptions = new ArrayList<>();
        concertOptions.add(new ConcertOption(1L, now, 10000.0));
        concertOptions.add(new ConcertOption(2L, now.plusHours(1), 20000.0));

        given(concertInfoManagementFacade.getConcertOption(token, concertId)).willReturn(concertOptions);

        List<ConcertDateResponse> responseData = new ArrayList<>();
        for (ConcertOption concertOption : concertOptions) {
            LocalDateTime concertDate = concertOption.getConcertDate();
            String date = concertDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    + concertDate.getHour() + concertDate.getMinute();
            ConcertDateResponse dummyData = ConcertDateResponse.builder()
                    .concertOptionId(concertOption.getConcertOptionId())
                    .concertDate(date)
                    .build();
            responseData.add(dummyData);
        }

        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", responseData);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        // when
        MockHttpServletRequestBuilder requestBuilder = get("/api/concert/{concertId}/available-dates", concertId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }



}