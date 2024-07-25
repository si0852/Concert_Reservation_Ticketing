package com.hhplus.concert_ticketing.infra;

import com.hhplus.concert_ticketing.business.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByUserIdAndSeatId(Long userId, Long seatId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select r from Reservation r where r.reservationId = :reservationId")
//    Optional<Reservation> findByReservationIdForUpdate(Long reservationId);
    List<Reservation> findAllByUserId(Long userId);
    List<Reservation> findAllByStatus(String status);
}
