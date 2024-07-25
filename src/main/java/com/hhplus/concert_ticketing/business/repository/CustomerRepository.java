package com.hhplus.concert_ticketing.business.repository;

import com.hhplus.concert_ticketing.business.entity.Customer;

public interface CustomerRepository {

    Customer saveData(Customer customer);

    Customer getCustomerData(Long customerId);

    Customer updateCharge(Customer customer);

    void deleteAll();
}
