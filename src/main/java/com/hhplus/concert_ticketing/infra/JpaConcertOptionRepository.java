package com.hhplus.concert_ticketing.infra;

import com.hhplus.concert_ticketing.business.entity.ConcertOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaConcertOptionRepository extends JpaRepository<ConcertOption, Long> {
    List<ConcertOption> findAllByConcertId(Long concertId);

    List<LocalDateTime> findConcertDateByConcertId(Long concertId);
}
