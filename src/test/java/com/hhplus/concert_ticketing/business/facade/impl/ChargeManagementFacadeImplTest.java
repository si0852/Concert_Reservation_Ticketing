package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.application.facade.impl.ChargeManagementFacadeImpl;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChargeManagementFacadeImplTest {

    @InjectMocks
    ChargeManagementFacadeImpl chargeManagementFacade;

    @Mock
    CustomerService customerService;

    @DisplayName("요청 금액이 1000보다 작을경우")
    @Test
    void validation_request_amount() {
        //given
        Long userId = 1L;
        Double amount = 900.0;

        // when&&then
        assertThrows(RuntimeException.class, () -> {
            chargeManagementFacade.chargingPoint(userId, amount);
        });
    }

}