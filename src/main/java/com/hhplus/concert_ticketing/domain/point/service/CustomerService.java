package com.hhplus.concert_ticketing.domain.point.service;

import com.hhplus.concert_ticketing.domain.point.entity.Customer;

public interface CustomerService {

    Customer saveCustomer(Customer customer);

    Customer getCustomerData(Long customerId);

    Customer updateCharge(Customer customer);
}
