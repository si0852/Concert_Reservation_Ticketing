package com.hhplus.concert_ticketing.infra.point;

import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCustomerRepository extends JpaRepository<Customer, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Customer c where c.customerId = :customerId")
    Optional<Customer> findByCustomerIdForUpdate(Long customerId);
}
