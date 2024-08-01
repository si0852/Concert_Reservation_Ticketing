package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.entity.ConcertOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaConcertOptionRepository extends JpaRepository<ConcertOption, Long> {
    List<ConcertOption> findAllByConcertId(Long concertId);

    Optional<ConcertOption> findByConcertOptionIdAndConcertDateGreaterThanEqual(Long concertOptionId, LocalDateTime now);

    List<LocalDateTime> findConcertDateByConcertId(Long concertId);
}
