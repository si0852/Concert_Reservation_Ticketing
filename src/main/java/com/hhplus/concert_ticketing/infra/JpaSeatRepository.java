package com.hhplus.concert_ticketing.infra;

import com.hhplus.concert_ticketing.business.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByConcertOptionIdAndSeatStatus(Long concertOptionId, String seatStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.seatId = :seatId")
    Optional<Seat> findBySeatIdForUpdate(Long seatId);
}
