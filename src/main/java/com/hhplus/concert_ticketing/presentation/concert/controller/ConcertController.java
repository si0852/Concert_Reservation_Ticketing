package com.hhplus.concert_ticketing.presentation.concert.controller;

import com.hhplus.concert_ticketing.application.concert.ConcertInfoManagementFacade;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.dto.SeatDto;
import com.hhplus.concert_ticketing.presentation.dto.response.ConcertDateResponse;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.presentation.dto.response.SeatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/concert")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertInfoManagementFacade concertInfoManagementFacade;


    @GetMapping
    @ResponseBody
    @Operation(summary = "콘서트 정보 조회", description = "콘서트 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "콘서트 정보가 없습니다./ 콘서트 상세 정보가 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getConcertData() {
        try {
            List<ConcertDto> concertData = concertInfoManagementFacade.getConcertData();
            return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", concertData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error", null));
        }
    }

    @GetMapping("/{concertId}/available-dates")
    @ResponseBody
    @Operation(summary = "콘서트 옵션 정보 조회", description = "콘서트 일자, 가격 등을 리턴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "이미 예약진행중인 데이터가 존재합니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "콘서트 정보가 없습니다./ 콘서트 상세 정보가 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "concertId", description = "concertId 값", example = "1")
    })
    public ResponseEntity<?> getAvailableDates(@PathVariable Long concertId)  throws Exception{
        List<ConcertOptionDto> concertOptions = concertInfoManagementFacade.getConcertOption(concertId);

        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", concertOptions));
    }

    // 예약 가능 좌석 조회
    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회 API", description = "선택한 콘서트 예약 좌석 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "이미 예약진행중인 데이터가 존재합니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "오픈된 콘서트 정보가 없습니다./ 예약가능한 좌석이 없습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버에러", content = @Content(mediaType = "application/json"))
    })
    @Parameters({
            @Parameter(name = "concertOptionId", description = "concertOptionId 값", example = "1")
    })
    @ResponseBody
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long concertOptionId ) throws Exception {
        List<SeatDto> seatData = concertInfoManagementFacade.getSeatData(concertOptionId);

        return ResponseEntity.ok(new ResponseDto(HttpServletResponse.SC_OK, "Success", seatData));
    }


}
