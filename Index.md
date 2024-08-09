## 개요
Concert, ConcertOption, Seat, Reservation, Payment 등 테이블 조회 성능을 높이기 위해 Index를 적용한다. Explain을 활용하여 Index를 적용한 테이블을 조회 성능이 어떻게 향상되는지 알아본다.

## 목적
  - 조회에 자주 사용되는 컬럼에 Index 적용
  - 카디널리티가 높은 컬럼 vs 카디널리티가 낮은 컬럼
  - Index 추가시 성능 분석
  - 추가/수정시 걸리는 시간 vs Index 적용 후 추가/수정시 걸리는 시간

## 테이블 별 데이터 수
  - Concert: 469 건
  - Concert_Option: 46,751 건
  - Seat: 2,337,501 건

## 1. 조회에 자주 사용되는 컬럼
  - 쿼리 : explain analyze select * from concert_option where concert_id = 468 
  - | |Index 설정 |Index 설정 X|
    |-----:|---|---|
    |cost|0.29, 10.20|0.00, 928.39|
    |Execution Time|0.051ms ~ 0.222ms|24.164ms|
    |Rows Removed by Fileter|46,713|46,713|

  - 쿼리: explain analyze select
    op.concert_id, op.concert_option_id  , op.concert_date , s.seat_number , s.seat_status
    from concert_option op inner join seat s
    on op.concert_option_id = s.concert_option_id and s.concert_option_id = 1;
    - 인덱스 설정: Seat -> concert_option_id
    - | |Index 설정 |Index 설정 X|
      |-----:|---|---|
      |cost|0.72, 18.22|1000.29, 32668.73|
      |Execution Time|0.192ms|92.117ms|
  - 결과: Index 적용 전/후의 Cost 비용과 실행시간이 확실히 차이가 난다.

## 2. 카디널리티
### 2.1 카디널리티가 높은 순으로 Index 추가(concert_id, price) vs 카디널리티가 낮은 순으로 Index 추가(price, concert_id)
  - concert_id는 price 컬럼에 비해 데이터 중복이 적어 카디널리티가 높다.
  - 쿼리1) explain analyze select * from concert_option where concert_id = 468;
  - | |Index(concert_id, price) |Index(price, concert_Id)|
    |-----:|---|---|
    |cost|0.41, 174.82|0.00, 928.39|
    |Execution Time|0.066ms ~ 0.077ms|3.492ms ~ 7.482ms|
  - 쿼리2) explain analyze select * from concert_option where price > 90000
  - | |Index(concert_id, price) |Index(price, concert_Id)|
        |-----:|---|---|
    |cost|0.41, 175.09|0.00, 928.39|
    |Execution Time|0.057ms|6.432ms|
  - 쿼리3)  explain analyze select * from concert_option where concert_id = 468 and price > 90000
  - | |Index(concert_id, price) |Index(price, concert_Id)|
            |-----:|---|---|
    |cost|0.41, 175.09|0.00, 1045.26|
    |Execution Time|0.132ms|2.909ms|
### 2.2 Index(concert_id, concert_date) vs Index(concert_date, concert_id)
  - concert_id는 concert_date 컬럼에 비해 데이터 중복이 적어 카디널리티가 높다.
  - 쿼리1) explain analyze select * from concert_option where concert_id = 468
  - | |Index(concert_id, concert_date) |Index(concert_date, concert_Id)|
                |-----:|---|---|
    |cost|0.29, 174.70|0.00, 928.39|
    |Execution Time|0.036ms ~ 0.227ms|3.272ms|
  - 쿼리2) explain analyze select * from concert_option where concert_date > now()
  - | |Index(concert_id, concert_date) |Index(concert_date, concert_Id)|
                    |-----:|---|---|
    |cost|0.00, 1045.26|0.00, 1045.26|
    |Execution Time|7.150ms|8.246ms|
  - 쿼리3) explain analyze select * from concert_option where concert_id = 468 and concert_date > now()
  - | |Index(concert_id, concert_date) |Index(concert_date, concert_Id)|
                        |-----:|---|---|
    |cost|0.29, 171.43|0.00, 1162.14|
    |Execution Time|0.090ms|2.668ms|
### 2.3 Seat 테이블 Index(concert_option_id, seat_status) 추가 vs 추가 X
  - 쿼리1)
    explain analyze select
    op.concert_id, op.concert_option_id  , op.concert_date , s.seat_number , s.seat_status
    from concert_option op
    inner join seat s
    on op.concert_option_id = s.concert_option_id and s.concert_option_id = 1
    where s.concert_option_id=1 and s.seat_status='AVAILABLE'
  - | |Index(concert_option_id, seat_status)  |Index(seat_status, concert_option_id) |설정X|
                             |-----:|---|---|---|
     |전체 cost|0.72, 111.11|1000.29. 35103,63|1000.29, 35103.63|
     |concert_option cost |0.29, 8.31|0.29, 8.31|0.29, 8.31|
     |seat cost|0.43, 102.26|0.00, 34089.38|0.00, 34089.38|
     |Execution Time|0.292ms|89.279ms|147.296ms|
### 2.4 결과
  - 카디널리티가 높은 컬럼과 낮은 컬럼을 추가하여 비교하였을 때 카디널리티가 높은 컬럼 조회 결과 성능이 더 좋을 것을 확인할 수 있었다.
  - 멀티 인덱스 비교
    - 카디널리티가 높은 컬럼이 앞에 있을때 vs 뒤에 있을때
    - 비교 결과 카디널리티가 높은 컬럼이 앞에 있을 때 더 좋은 나은 성능이 나왔다.
    - 하지만 ConcertOption 테이블의 경우, concert_id 컬럼만 index에 추가했을때 더 나은 성능이 보장된다.

## 3. Insert/Update 쿼리
### 3.1 비교
- Insert Query: explain analyze insert into concert_option(concert_option_id,concert_date, concert_id, price) values(46768, now(), 468, 100000)
- | |인덱스X  |Index(concert_id) |Index(concert_id, price)| index(price, concert_id)
                               |-----:|---|---|---|---|
  |cost|0.00, 0.01|0.00, 0.01|0.00, 0.01|0.00, 0.01|
  |Execution Time |0.289ms|0.261ms|0.544ms|3.344ms|
- Update Query: explain analyze update concert_option  set price=110000 where concert_option_id = 46768
- | |인덱스X  |Index(concert_id) |Index(concert_id, price)| index(price, concert_id)
                                 |-----:|---|---|---|---|
  |cost|0.00, 931.09|0.29, 10.20|0.41, 174.82|0.00, 929.39|
  |Execution Time |2.360ms|0.264ms|2.883ms|4.086ms|
### 3.1 결과
 - Insert/Update의 경우 Index만 적절히 잘 사용해준다면 성능이 향상된다.
 - 카디널리티가 낮은 값이 Index 앞에 설정이 되었다면, 성능이 악화된다.
 - Index를 적절히 사용한다면 Insert/Update가 자주 발생되는 컬럼이더라도 조회 성능 차이보다는 작기 때문에 조회 기준으로 Index를 설정하는 것이 좋다

## 4. Index 설정
  1. ConcertOption 테이블
     - Index: concert_id
     - price와 concert_date를 index로 추가하였을때 카디널리티가 낮아 성능이 낮아진다.
  2. Seat 테이블
     - Index: concert_option_id
     - seat_status의 경우 카디널리티가 낮아 위의 결과처럼 성능이 낮아질 가능성이 높다.
  3. Reservation 테이블
     - Index: seat_id, user_id
     - 해당 테이블은 Update/Insert 쿼리가 빈번하게 발생될 것으로 보이지만, 설정한 Index의 카디널리티가 높아 조회 성능에 맞춰서 Index를 설정하는 것이 좋다