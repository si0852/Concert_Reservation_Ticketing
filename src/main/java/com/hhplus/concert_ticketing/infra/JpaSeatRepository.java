package com.hhplus.concert_ticketing.infra;

import com.hhplus.concert_ticketing.business.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByConcertOptionId(Long concertOptionId);
}
