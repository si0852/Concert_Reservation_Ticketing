package com.hhplus.concert_ticketing.domain.point.service.impl;

import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import com.hhplus.concert_ticketing.domain.point.repository.CustomerRepository;
import com.hhplus.concert_ticketing.domain.point.service.CustomerService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.NoInfoException;
import jakarta.servlet.http.HttpServletResponse;
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
        Customer customerData = customerRepository.getCustomerData(customerId);
        if(customerData == null) throw new NoInfoException(new ResponseDto(HttpServletResponse.SC_NOT_FOUND, "유저가 존재하지 않습니다.", null));;
        return customerData;
    }

    @Override
    public Customer updateCharge(Customer customer) {
        return customerRepository.updateCharge(customer);
    }
}
