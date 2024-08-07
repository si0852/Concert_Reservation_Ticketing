package com.hhplus.concert_ticketing.util;

import com.hhplus.concert_ticketing.domain.concert.entity.Concert;
import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import com.hhplus.concert_ticketing.infra.concert.impl.ConcertOptionRepositoryImpl;
import com.hhplus.concert_ticketing.infra.concert.impl.ConcertRepositoryImpl;
import com.hhplus.concert_ticketing.infra.concert.impl.SeatRepositoryImpl;
import com.hhplus.concert_ticketing.status.SeatStatus;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Random;

//@Configuration
public class DataInitializer {

    private final ConcertRepositoryImpl concertRepositoryImpl;
    private final ConcertOptionRepositoryImpl concertOptionRepositoryImpl;
    private final SeatRepositoryImpl seatRepositoryImpl;


    public DataInitializer(ConcertRepositoryImpl concertRepositoryImpl, ConcertOptionRepositoryImpl concertOptionRepositoryImpl, SeatRepositoryImpl seatRepositoryImpl) {
        this.concertRepositoryImpl = concertRepositoryImpl;
        this.concertOptionRepositoryImpl = concertOptionRepositoryImpl;
        this.seatRepositoryImpl = seatRepositoryImpl;
    }

//    @Bean
    public CommandLineRunner concertData() {
        return (args) -> {
            Faker faker = new Faker(new Locale("ko", "KOREA"));
            Random random = new Random();
            for (int i = 1; i <= 1000; i++) {
                // 콘서트 이름 생성
                String concertName = faker.esports().league() + " 콘서트";
                Concert concert = new Concert(concertName);
                concertRepositoryImpl.saveData(concert);

                // 콘서트에 대해 10개의 임의 날짜 생성
                for (int j = 0; j < 100; j++) {
                    // 랜덤 날짜 생성
                    LocalDate today = LocalDate.now();
                    LocalDate randomDate = today.plusDays(random.nextInt(365)); // 오늘로부터 최대 1년 후의 날짜

                    // 랜덤 시간 생성 (2시부터 7시)
                    int hour = 2 + random.nextInt(6); // 2부터 7까지의 시간

                    // LocalDateTime 생성 (정각)
                    LocalDateTime localDateTime = LocalDateTime.of(randomDate, LocalTime.of(hour, 0));

                    // 최소 100,000원 이상, 최대 200,000원 이하의 만원 단위 가격 생성
                    int minPrice = 100000;
                    int maxPrice = 200000;
                    double price = minPrice + (random.nextInt((maxPrice - minPrice) / 10000 + 1)) * 10000;
                    ConcertOption concertOption = new ConcertOption(concert.getConcertId(), localDateTime, price);

                    concertOptionRepositoryImpl.saveData(concertOption);


                    // 좌석 생성
                    for (int k = 1; k <= 50; k++) {
                        String seatNumber = String.format("Seat-%03d", k);
                        Seat seat = new Seat(concertOption.getConcertOptionId(), seatNumber, SeatStatus.AVAILABLE.toString());

                        seatRepositoryImpl.saveData(seat);
                    }
                }
            }
        };
    }
}