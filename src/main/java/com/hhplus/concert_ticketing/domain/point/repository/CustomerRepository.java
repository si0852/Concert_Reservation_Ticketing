package com.hhplus.concert_ticketing.domain.point.repository;

import com.hhplus.concert_ticketing.domain.point.entity.Customer;

public interface CustomerRepository {

    Customer saveData(Customer customer);

    Customer getCustomerData(Long customerId);

    Customer updateCharge(Customer customer);
}
