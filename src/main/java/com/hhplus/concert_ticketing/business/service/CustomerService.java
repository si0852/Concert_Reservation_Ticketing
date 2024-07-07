package com.hhplus.concert_ticketing.business.service;

import com.hhplus.concert_ticketing.business.entity.Customer;

public interface CustomerService {

    Customer saveCustomer(Customer customer);

    Customer getCustomerData(Long customerId);

    Customer updateCharge(Customer customer);
}
