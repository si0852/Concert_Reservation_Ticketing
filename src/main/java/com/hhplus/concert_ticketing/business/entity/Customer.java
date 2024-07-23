package com.hhplus.concert_ticketing.business.entity;

import com.hhplus.concert_ticketing.presentation.dto.response.ResponseDto;
import com.hhplus.concert_ticketing.util.exception.BadRequestException;
import com.hhplus.concert_ticketing.util.exception.InSufficientBalanceException;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long customerId;

    String userName;
    Double balance;

    @Version
    private Long version;

    public Customer(String userName, Double balance) {
        this.userName = userName;
        this.balance = balance;
    }

    private void setBalance(Double balance) {
        this.balance = balance;
    }

    public void subtractBalance(Double ticketPrice) {
        Double userPoint = this.getBalance() - ticketPrice;
        if (userPoint < 0 ) throw new InSufficientBalanceException(new ResponseDto(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"잔액이 부족합니다.", userPoint));
        this.balance = userPoint;
    }

    public void chargePoint(Double point) {
        if(point < 1000) throw new BadRequestException(new ResponseDto(HttpServletResponse.SC_BAD_REQUEST, "1000원 이상의 금액을 충전해주세요", point));
        Double total = this.getBalance() + point;
        setBalance(total);
    }
}
