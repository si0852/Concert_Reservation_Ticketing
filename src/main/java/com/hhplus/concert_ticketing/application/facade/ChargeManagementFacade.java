package com.hhplus.concert_ticketing.application.facade;

import com.hhplus.concert_ticketing.business.entity.Customer;

public interface ChargeManagementFacade {

    Customer chargingPoint(Long userId, Double amount);

    Customer getCustomerData(Long userId);

}
