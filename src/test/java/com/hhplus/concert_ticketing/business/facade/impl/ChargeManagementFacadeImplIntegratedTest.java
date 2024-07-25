package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.application.facade.ChargeManagementFacade;
import com.hhplus.concert_ticketing.business.repository.CustomerRepository;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ChargeManagementFacadeImplIntegratedTest {

    private static final Logger log = LoggerFactory.getLogger(ChargeManagementFacadeImplIntegratedTest.class);
    @Autowired
    ChargeManagementFacade chargeManagementFacade;
    @Autowired
    CustomerService customerService;
    @Autowired
    CustomerRepository customerRepository;


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
    void update_check() throws Exception {
        //given
        Double amount = 1900.0;
        Customer customer2 = new Customer("sihyun2",  0.0);
        Customer saveCustomer = customerService.saveCustomer(customer2);

        // when
        chargeManagementFacade.chargingPoint(saveCustomer.getCustomerId(), amount);
        Customer customerData = chargeManagementFacade.getCustomerData(saveCustomer.getCustomerId());

        // then
        assertThat(customerData.getBalance()).isEqualTo(1900.0);

    }

    @DisplayName("동시성 테스트 - 포인트 충전, 비관적락")
    @Test
    void concurrent_test() throws Exception, InterruptedException{
        Customer customer = new Customer("hey", 10000.0);
        Customer saveCustomer = customerRepository.saveData(customer);

        int numberOfThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);


        Runnable task = () -> {
            long startTime = System.nanoTime();
            try {
                chargeManagementFacade.chargingPoint(saveCustomer.getCustomerId(), 9000.0);
            } catch (Exception e) {
                log.error("Exception in task", e);
            } finally {
                long endTime = System.nanoTime();
                log.info("Execution time: {} ms", (endTime - startTime) / 1_000_000);
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
        assertThat(customerData.getBalance()).isGreaterThan(10000.0);

    }

}