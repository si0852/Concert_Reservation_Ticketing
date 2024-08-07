package com.hhplus.concert_ticketing.application.point.impl;

import com.hhplus.concert_ticketing.application.point.ChargeManagementFacade;
import com.hhplus.concert_ticketing.domain.point.entity.Customer;
import com.hhplus.concert_ticketing.domain.point.service.CustomerService;
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
    public Customer chargingPoint(Long userId, Double amount) throws Exception{
        Customer currentCustomerData = customerService.getCustomerData(userId);
        currentCustomerData.chargePoint(amount);

        return customerService.updateCharge(currentCustomerData);
    }

    @Transactional
    @Override
    public Customer getCustomerData(Long userId) throws Exception{
        return customerService.getCustomerData(userId);
    }
}
