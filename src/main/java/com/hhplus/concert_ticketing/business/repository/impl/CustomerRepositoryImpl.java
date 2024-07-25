package com.hhplus.concert_ticketing.business.repository.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.business.repository.CustomerRepository;
import com.hhplus.concert_ticketing.infra.JpaCustomerRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpaCustomerRepository;

    public CustomerRepositoryImpl(JpaCustomerRepository jpaCustomerRepository) {
        this.jpaCustomerRepository = jpaCustomerRepository;
    }

    @Override
    public Customer saveData(Customer customer) {
        return jpaCustomerRepository.save(customer);
    }

    @Override
    public Customer getCustomerData(Long customerId) {
        return jpaCustomerRepository.findById(customerId).orElse(null);
    }

    @Override
    public Customer updateCharge(Customer customer) {
        return jpaCustomerRepository.save(customer);
    }

    @Override
    public void deleteAll() {
        jpaCustomerRepository.deleteAll();
    }
}
