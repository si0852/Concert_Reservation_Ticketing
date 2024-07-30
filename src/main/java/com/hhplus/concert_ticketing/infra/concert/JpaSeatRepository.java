package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByConcertOptionIdAndSeatStatus(Long concertOptionId, String seatStatus);
}
