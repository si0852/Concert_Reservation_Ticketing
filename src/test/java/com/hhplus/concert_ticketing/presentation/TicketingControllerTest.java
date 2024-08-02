package com.hhplus.concert_ticketing.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.application.concert.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.application.payment.PaymentManagementFacade;
import com.hhplus.concert_ticketing.application.point.ChargeManagementFacade;
import com.hhplus.concert_ticketing.application.queue.StatusManagementFacade;
import com.hhplus.concert_ticketing.application.queue.TokenManagementFacade;
import com.hhplus.concert_ticketing.application.reservation.ReservationManagementFacade;
import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.domain.concert.service.ConcertService;
import com.hhplus.concert_ticketing.domain.queue.entity.Token;
import com.hhplus.concert_ticketing.domain.queue.service.TokenService;
import com.hhplus.concert_ticketing.infra.queue.JpaTokenRepository;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.SeatDto;
import com.hhplus.concert_ticketing.presentation.dto.response.*;
import com.hhplus.concert_ticketing.status.SeatStatus;
import com.hhplus.concert_ticketing.status.TokenStatus;
import com.hhplus.concert_ticketing.util.TokenAuthInterceptor;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketingControllerTest {
    private static final Logger log = LoggerFactory.getLogger(TicketingControllerTest.class);

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;


    @Autowired
    TokenAuthInterceptor tokenAuthInterceptor;
    @Autowired
    TokenManagementFacade tokenManagementFacade;
    @Autowired
    ChargeManagementFacade chargeManagementFacade;
    @Autowired
    PaymentManagementFacade paymentManagementFacade;
    @Autowired
    ReservationManagementFacade reservationManagementFacade;
    @Autowired
    StatusManagementFacade statusManagementFacade;
    @Autowired
    ConcertInfoManagementFacade concertInfoManagementFacade;
    @Autowired
    TokenService tokenService;
    @Autowired
    ConcertService concertService;

    @Autowired
    JpaTokenRepository jpaTokenRepository;



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

    @DisplayName("유저 토큰 포지션 API")
    @Test
    void 유저_토큰_포지션() throws Exception {
        Long userId = 1L;
        String tokenStr = "sdfasdfsadf";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(1);

        Token token = new Token(userId, tokenStr, TokenStatus.WAITING.toString(), LocalDateTime.now(), expiresAt);

        Token saveToken = tokenService.saveToken(token);
        Integer tokenPosition = tokenManagementFacade.getTokenPosition(saveToken.getToken());


        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", tokenPosition);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/token/position")
                .param("userId", String.valueOf(saveToken.getUserId()))
                .header("Authorization", saveToken.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @DisplayName("유저 토큰 상태 확인을 위한 API")
    @Test
    void 유저_토큰_상태_확인() throws Exception {
        Long userId = 1L;
        String tokenStr = "sdfasdfsadf";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(1);

        Token token = new Token(userId, tokenStr, TokenStatus.WAITING.toString(), LocalDateTime.now(), expiresAt);

        Token saveToken = tokenService.saveToken(token);
        Integer tokenPosition = tokenManagementFacade.getTokenPosition(saveToken.getToken());
        TokenResponse res = new TokenResponse(saveToken.getToken(), tokenPosition, saveToken.getExpiresAt());

        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", res);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/token/info")
                .param("userId", String.valueOf(saveToken.getUserId()))
                .header("Authorization", saveToken.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

    }

    @DisplayName("예약 가능 날짜 조회를 위한 API")
    @Test
    void 예약_가능_날짜_조회() throws Exception {
        String tokenStr = "efe120d8-830d-4fd8-8e07-59114160d3fc";
        LocalDateTime now = LocalDateTime.now();

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), now, 10000.0));
        concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), now, 10000.0));

        List<ConcertOptionDto> concertOptions = concertInfoManagementFacade.getConcertOption(concert.getConcertId());

        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", concertOptions);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/concert/{concertId}/available-dates",concert.getConcertId())
                .param("token", (tokenStr))
                .header("Authorization", tokenStr)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));


    }

    @DisplayName("예약 가능 좌석 조회를 위한 API")
    @Test
    void 예약_가능_좌석_조회() throws Exception {
        String tokenStr = "efe120d8-830d-4fd8-8e07-59114160d3fc";
        LocalDateTime now = LocalDateTime.now();

        Concert concert = concertService.saveConcertData(new Concert("Concert"));
        ConcertOption saveData = concertService.saveConcertOption(new ConcertOption(concert.getConcertId(), now, 10000.0));
        ConcertOption concertOption = new ConcertOption(saveData.getConcertId(), now.plusDays(1), 10000.0);
        ConcertOption saveOption = concertService.saveConcertOption(concertOption);
        Seat seat1 = new Seat(saveOption.getConcertOptionId(), "1A", SeatStatus.AVAILABLE.toString());
        concertService.saveSeatData(seat1);
        Seat seat2 = new Seat(saveOption.getConcertOptionId(), "2A", SeatStatus.AVAILABLE.toString());
        concertService.saveSeatData(seat2);

        List<SeatDto> Seats = concertInfoManagementFacade.getSeatData(saveOption.getConcertOptionId());

        ResponseDto expectedResponse = new ResponseDto(HttpServletResponse.SC_OK, "Success", Seats);
        String expectedJson = mapper.writeValueAsString(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/concert/{concertOptionId}/available-seats",concertOption.getConcertOptionId())
                .param("token", (tokenStr))
                .header("Authorization", tokenStr)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }



}