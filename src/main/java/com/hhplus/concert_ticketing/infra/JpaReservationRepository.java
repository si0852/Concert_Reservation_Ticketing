package com.hhplus.concert_ticketing.infra;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUserIdAndSeatId(Long userId, Long seatId);
    List<Reservation> findAllByUserId(Long userId);
}
