package com.hhplus.concert_ticketing.infra.point;

import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCustomerRepository extends JpaRepository<Customer, Long> {
}
