package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.application.facade.ChargeManagementFacade;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChargeManagementFacadeImpl implements ChargeManagementFacade {

    private final CustomerService customerService;

    public ChargeManagementFacadeImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Transactional
    @Override
    public Customer chargingPoint(Long userId, Double amount) {
        if(amount < 1000) throw new RuntimeException("1000원 이상의 금액을 충전해주세요");
        Customer currentCustomerData = customerService.getCustomerData(userId);
        Double total = amount + currentCustomerData.getBalance();
        currentCustomerData.setBalance(total);

        return customerService.updateCharge(currentCustomerData);
    }

    @Transactional
    @Override
    public Customer getCustomerData(Long userId) {
        return customerService.getCustomerData(userId);
    }
}
