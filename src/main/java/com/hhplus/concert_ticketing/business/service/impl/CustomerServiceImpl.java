package com.hhplus.concert_ticketing.business.service.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.business.repository.CustomerRepository;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import jakarta.persistence.LockModeType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        return customerRepository.saveData(customer);
    }

    @Override
    @Transactional
    public Customer getCustomerData(Long customerId) {
        Customer customerData = customerRepository.getCustomerData(customerId);
        if(customerData == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "유저가 존재하지 않습니다.", null));;
        return customerData;
    }

    @Override
    public Customer updateCharge(Customer customer) {
        return customerRepository.updateCharge(customer);
    }
}
