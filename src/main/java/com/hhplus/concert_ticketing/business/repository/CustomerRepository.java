package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface CustomerRepository {

    Customer saveData(Customer customer);
    Customer getCustomerData(Long customerId);
    Customer updateCharge(Customer customer);
}
