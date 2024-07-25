package com.hhplus.concert_ticketing.business.facade.impl;

import com.hhplus.concert_ticketing.business.entity.Customer;
import com.hhplus.concert_ticketing.application.facade.ChargeManagementFacade;
import com.hhplus.concert_ticketing.business.entity.Token;
import com.hhplus.concert_ticketing.business.repository.CustomerRepository;
import com.hhplus.concert_ticketing.business.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
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

    private List<Long> userIdList = new ArrayList<>();

    @BeforeEach
    void before() {
        for (int i = 0; i < 11; i++) {
            Customer customer = new Customer("s"+i, i*1000.0);
            Customer customer1 = customerRepository.saveData(customer);
            userIdList.add(customer1.getCustomerId());
        }
    }

    @AfterEach
    void after() {
        customerRepository.deleteAll();
    }


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

    @DisplayName("분산락 테스트")
    @Test
    void 분산락_테스트() throws Exception {
        Long customerId = userIdList.get(1);
        Double amount1 = 1000.0;
        Double amount2 = 1200.0;
        Double amount3 = 1300.0;

        Thread t1 = new Thread(()  -> {
            try {
                chargeManagementFacade.chargingPoint(customerId, amount1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(()  -> {
            try {
                chargeManagementFacade.chargingPoint(customerId, amount2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(()  -> {
            try {
                chargeManagementFacade.chargingPoint(customerId, amount3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        Thread.sleep(50);
        t2.start();
        Thread.sleep(50);
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        Customer customerData =
                customerService.getCustomerData(userIdList.get(1));
        log.info("customerData : " +customerData.getBalance());
        assertThat(customerData.getBalance()).isGreaterThan(1000.0);
    }

    @DisplayName("분산락 테스트")
    @Test
    void 분산락_테스트2() throws Exception {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Runnable task = () -> {
            long startTime = System.nanoTime();
            try {
                Customer customer = chargeManagementFacade.chargingPoint(userIdList.get(0), 1000.0);
                log.info("customer point : " + customer.getBalance());
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
        executorService.shutdown();

        Customer customerData =
                customerService.getCustomerData(userIdList.get(0));
        log.info("customerData : " +customerData.getBalance());
        assertThat(customerData.getBalance()).isGreaterThan(1000.0);
    }

}