package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.application.facade.ChargeManagementFacade;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ChargeManagementFacadeImplIntegratedTest {

    @Autowired
    ChargeManagementFacade chargeManagementFacade;
    @Autowired
    CustomerService customerService;


    @DisplayName("요청 금액이 1000보다 작을경우")
    @Test
    void validation_request_amount() {
        //given
        Customer customer = new Customer("sihyun", 0.0);
        Customer saveCustomer = customerService.saveCustomer(customer);
        Long userId = saveCustomer.getCustomerId();
        Double amount = 900.0;

        // when && then
        assertThrows(RuntimeException.class, () -> {
            chargeManagementFacade.chargingPoint(userId, amount);
        });
    }


    @DisplayName("요청 금액 update")
    @Test
    void update_check() {
        //given
        Double amount = 1900.0;
        Customer customer2 = new Customer("sihyun2", 0.0);
        Customer saveCustomer = customerService.saveCustomer(customer2);

        // when
        chargeManagementFacade.chargingPoint(saveCustomer.getCustomerId(), amount);
        Customer customerData = chargeManagementFacade.getCustomerData(saveCustomer.getCustomerId());

        // then
        assertThat(customerData.getBalance()).isEqualTo(1900.0);

    }

}