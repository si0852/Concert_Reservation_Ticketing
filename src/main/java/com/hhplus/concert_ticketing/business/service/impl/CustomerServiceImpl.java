package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.business.repository.CustomerRepository;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.saveData(customer);
    }

    @Override
    public Customer getCustomerData(Long customerId) {
        return customerRepository.getCustomerData(customerId);
    }

    @Override
    public Customer updateCharge(Customer customer) {
        return customerRepository.updateCharge(customer);
    }
}
