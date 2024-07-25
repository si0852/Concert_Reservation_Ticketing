package com.hhplus.concert_ticketing.application.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.application.facade.ChargeManagementFacade;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import com.hhplus.concert_ticketing.business.service.TokenService;
import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.LockException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ChargeManagementFacadeImpl implements ChargeManagementFacade {

    private final CustomerService customerService;
    private final RedissonClient redissonClient;

    public ChargeManagementFacadeImpl(CustomerService customerService, RedissonClient redissonClient) {
        this.customerService = customerService;
        this.redissonClient = redissonClient;
    }

    @Transactional
    @Override
    public Customer chargingPoint(Long userId, Double amount) {
        String token = "asdfasdfaa";
        RLock lock = redissonClient.getLock(token+userId);
        try {
            boolean isLocked = lock.tryLock(100, 10000, TimeUnit.MILLISECONDS);
            log.info("is Lock : " + isLocked );
            if(!isLocked) throw new LockException(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lock 획득 실패", null));
            Customer currentCustomerData = customerService.getCustomerData(userId);
            currentCustomerData.chargePoint(amount);
            log.info("Point Lock : " + currentCustomerData.getBalance());
            return customerService.updateCharge(currentCustomerData);
        } catch (Exception e) {
            throw new RuntimeException("Error : " + e.getMessage());
        }finally {
            lock.unlock();
        }

    }

    @Transactional
    @Override
    public Customer getCustomerData(Long userId) throws Exception{
        return customerService.getCustomerData(userId);
    }
}
