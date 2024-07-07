package com.hhplus.concert_ticketing.infra;

import com.hhplus.concert_ticketing.business.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaConcertRepository extends JpaRepository<Concert, Long> {
}
