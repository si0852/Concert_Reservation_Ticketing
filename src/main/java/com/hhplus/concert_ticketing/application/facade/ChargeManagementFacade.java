package com.hhplus.concert_ticketing.application.facade;

import com.hhplus.concert_ticketing.domain.point.entity.Customer;

public interface ChargeManagementFacade {

    Customer chargingPoint(Long userId, Double amount) throws Exception;

    Customer getCustomerData(Long userId)  throws Exception;

}
