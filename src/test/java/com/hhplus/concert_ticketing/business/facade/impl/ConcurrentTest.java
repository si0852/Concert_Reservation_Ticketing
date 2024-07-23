package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.application.facade.ChargeManagementFacade;
import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.business.repository.CustomerRepository;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class ConcurrentTest {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentTest.class);

    @Autowired
    ChargeManagementFacade chargeManagementFacade;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @DisplayName("동시성 테스트")
    @Test
    void concurrent_test() throws Exception, InterruptedException{
        Customer customer = new Customer("hey", 10000.0);
        Customer saveCustomer = customerRepository.saveData(customer);

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);


        Runnable task = () -> {
            try {
                chargeManagementFacade.chargingPoint(saveCustomer.getCustomerId(), 9000.0);
            } catch (Exception e) {
                log.error("Exception in task", e);
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(task);
        }

        latch.await(); // await() 메서드를 사용하여 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown(); // 스레드 종료

        Customer customerData = customerService.getCustomerData(saveCustomer.getCustomerId());
        log.info("customerData : " +customerData.getBalance());

        Assertions.assertThat(saveCustomer.getBalance()).isEqualTo(1);

    }

}
