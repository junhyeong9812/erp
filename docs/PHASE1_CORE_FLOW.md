# Phase 1: 핵심 금전 흐름

> inventory → payment → settlement

## 목표

물건이 있어야 팔 수 있고, 팔아야 돈이 들어오고, 돈이 들어와야 정산할 수 있다.
이 흐름이 ERP의 심장이다.

---

## 구현 순서

```
1. inventory (재고)  - 물건이 있어야 팔 수 있다
2. payment (결제)    - 팔면 돈을 받아야 한다
3. settlement (정산) - 받은 돈을 정리해야 한다
```

---

## 1. Inventory (재고/창고)

### 왜 먼저인가?
- 결제하려면 "뭘 샀는지"가 있어야 함
- "뭘 샀는지"는 재고에서 나감
- 재고가 없으면 판매 자체가 불가능

### 핵심 엔티티

```
Product (상품 마스터)
├── id
├── name
├── sku (재고관리코드)
├── price
├── category
└── status (ACTIVE, DISCONTINUED)

Warehouse (창고)
├── id
├── name
├── location
└── type (MAIN, SUB, RETURN)

Stock (재고)
├── id
├── product_id (FK)
├── warehouse_id (FK)
├── quantity (현재 수량)
├── reserved_quantity (예약된 수량)
├── available_quantity (가용 수량 = quantity - reserved)
└── updated_at

StockMovement (재고 이동 이력)
├── id
├── stock_id (FK)
├── type (IN, OUT, ADJUST, TRANSFER)
├── quantity
├── reason
├── reference_type (PURCHASE, SALE, RETURN, ADJUST)
├── reference_id
└── created_at

Lot (로트 - 선택)
├── id
├── product_id (FK)
├── lot_number
├── manufactured_date
├── expiry_date
└── quantity
```

### 핵심 시나리오 (구현 순서)

#### 1-1. 상품 등록
```
시나리오: 새 상품을 시스템에 등록한다
입력: 상품명, SKU, 가격, 카테고리
출력: 상품 ID
검증: SKU 중복 불가
```

#### 1-2. 창고 등록
```
시나리오: 창고를 등록한다
입력: 창고명, 위치, 타입
출력: 창고 ID
```

#### 1-3. 입고 처리
```
시나리오: 상품이 창고에 들어온다
입력: 상품ID, 창고ID, 수량, 입고사유(구매/반품/조정)
처리:
  1. Stock 조회 (없으면 생성)
  2. quantity 증가
  3. StockMovement 기록 (type=IN)
출력: 변경된 재고 정보
```

#### 1-4. 재고 조회
```
시나리오: 특정 상품의 재고를 확인한다
입력: 상품ID, (선택)창고ID
출력: 총 수량, 예약 수량, 가용 수량
```

#### 1-5. 재고 예약 ⭐ (동시성 핵심)
```
시나리오: 주문 시 재고를 미리 확보한다
입력: 상품ID, 창고ID, 수량
처리:
  1. 가용 수량 확인 (quantity - reserved >= 요청수량)
  2. reserved_quantity 증가
  3. StockMovement 기록 (type=RESERVE)
출력: 예약 성공/실패
주의: 동시에 여러 주문이 오면? → 락 필요
```

#### 1-6. 출고 처리
```
시나리오: 예약된 재고를 실제로 출고한다
입력: 상품ID, 창고ID, 수량, 출고사유
처리:
  1. reserved_quantity 감소
  2. quantity 감소
  3. StockMovement 기록 (type=OUT)
출력: 변경된 재고 정보
선행조건: 예약이 되어 있어야 함
```

#### 1-7. 예약 취소
```
시나리오: 주문 취소 시 예약을 풀어준다
입력: 상품ID, 창고ID, 수량
처리:
  1. reserved_quantity 감소
  2. StockMovement 기록 (type=RELEASE)
출력: 변경된 재고 정보
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 재고 차감 시점 | 주문 시 즉시 차감 | 출고 시 차감 | 예약 개념 필요 여부 |
| 동시성 제어 | 비관적 락 | 낙관적 락 | 충돌 빈도 |
| 재고 음수 허용 | 허용 (백오더) | 불허 | 비즈니스 정책 |
| FIFO 적용 | 로트 단위 관리 | 총량만 관리 | 유통기한 중요도 |

### 먼저 답해야 할 질문
- [ ] 재고가 0인데 주문이 들어오면? (백오더 허용?)
- [ ] 예약 후 결제 실패하면 예약은 언제 풀리나?
- [ ] 창고가 여러 개면 어느 창고에서 출고?

---

## 2. Payment (결제/수금)

### 왜 두 번째인가?
- 재고가 있어야 "팔 수 있음"
- 팔았으면 돈을 받아야 함
- 돈 받는 게 결제

### 핵심 엔티티

```
Order (주문)
├── id
├── order_number
├── customer_id
├── status (PENDING, PAID, SHIPPED, COMPLETED, CANCELLED)
├── total_amount
├── paid_amount
├── created_at
└── updated_at

OrderItem (주문 상세)
├── id
├── order_id (FK)
├── product_id (FK)
├── quantity
├── unit_price
├── subtotal
└── stock_reserved (예약 여부)

Payment (결제)
├── id
├── order_id (FK)
├── payment_method (CARD, BANK, VIRTUAL_ACCOUNT)
├── amount
├── status (PENDING, COMPLETED, FAILED, REFUNDED)
├── pg_transaction_id (외부 PG 거래 ID)
├── paid_at
└── created_at

Refund (환불)
├── id
├── payment_id (FK)
├── order_id (FK)
├── amount
├── reason
├── status (PENDING, COMPLETED, FAILED)
├── refunded_at
└── created_at
```

### 핵심 시나리오 (구현 순서)

#### 2-1. 주문 생성
```
시나리오: 고객이 상품을 주문한다
입력: 고객ID, [{상품ID, 수량}]
처리:
  1. 각 상품 재고 확인 (가용 수량 체크)
  2. 재고 예약 (inventory.reserve)
  3. Order, OrderItem 생성
  4. 주문 상태 = PENDING
출력: 주문 ID, 총액
실패 시: 예약된 재고 롤백
```

#### 2-2. 결제 요청
```
시나리오: 주문에 대해 결제를 진행한다
입력: 주문ID, 결제수단, 금액
처리:
  1. 주문 상태 확인 (PENDING인지)
  2. Payment 생성 (status=PENDING)
  3. (외부 PG 연동 가정) PG 결제 요청
  4. 결제 성공 시:
     - Payment status = COMPLETED
     - Order paid_amount 증가
     - 전액 결제 시 Order status = PAID
출력: 결제 결과
```

#### 2-3. 부분 결제 ⭐
```
시나리오: 총액의 일부만 결제한다
입력: 주문ID, 결제금액
처리:
  1. paid_amount + 결제금액 <= total_amount 확인
  2. Payment 생성
  3. paid_amount 누적
  4. 전액 도달 시 status = PAID
출력: 남은 결제 금액
```

#### 2-4. 부분 취소/환불 ⭐⭐ (복잡도 높음)
```
시나리오: 주문의 일부 상품만 취소한다
입력: 주문ID, [{상품ID, 취소수량}]
처리:
  1. OrderItem에서 해당 상품 찾기
  2. 취소 금액 계산
  3. 재고 예약 해제 (inventory.release)
  4. Refund 생성
  5. 환불 처리 (PG 연동)
  6. 전체 취소 시 Order status = CANCELLED
출력: 환불 금액
주의: 이미 출고된 상품은? → 반품 프로세스 필요
```

#### 2-5. 결제 실패 처리
```
시나리오: PG 결제가 실패했다
입력: 주문ID, 실패사유
처리:
  1. Payment status = FAILED
  2. 일정 시간 후 재고 예약 해제? (정책 결정 필요)
출력: 실패 안내
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 결제-재고 순서 | 결제 먼저 | 재고 예약 먼저 | 결제 실패 시 UX |
| 부분취소 환불 | 즉시 환불 | 배치 환불 | PG 수수료 정책 |
| 결제 타임아웃 | 예약 자동 해제 | 수동 해제 | 운영 리소스 |
| 복합 결제 | 허용 (카드+포인트) | 단일 수단만 | 복잡도 vs 유연성 |

### 먼저 답해야 할 질문
- [ ] 결제 대기 중 재고 예약은 얼마나 유지?
- [ ] 부분 결제 후 나머지를 안 내면?
- [ ] 환불은 원래 결제 수단으로만?

---

## 3. Settlement (정산/회계)

### 왜 세 번째인가?
- 돈을 받았으면 정리해야 함
- 누구한테 얼마 줘야 하는지, 수수료는 얼마인지
- 이게 회계의 기본

### 핵심 엔티티

```
Ledger (원장)
├── id
├── type (SALES, PURCHASE, REFUND, FEE)
├── reference_type (ORDER, PAYMENT, REFUND)
├── reference_id
├── debit (차변 - 자산 증가, 비용 발생)
├── credit (대변 - 부채 증가, 수익 발생)
├── balance (잔액)
├── description
└── created_at

SettlementPeriod (정산 기간)
├── id
├── start_date
├── end_date
├── status (OPEN, CLOSED, SETTLED)
└── closed_at

SellerSettlement (판매자 정산 - 마켓플레이스용)
├── id
├── seller_id
├── period_id (FK)
├── total_sales (총 매출)
├── commission (수수료)
├── refund_amount (환불 차감)
├── net_amount (정산 금액 = 매출 - 수수료 - 환불)
├── status (PENDING, CALCULATED, PAID)
└── paid_at

Invoice (세금계산서)
├── id
├── invoice_number
├── type (SALES, PURCHASE)
├── customer_id / supplier_id
├── amount
├── tax_amount
├── issued_at
└── period_id
```

### 핵심 시나리오 (구현 순서)

#### 3-1. 매출 전표 자동 생성
```
시나리오: 결제 완료 시 매출 전표가 생긴다
트리거: Payment.status = COMPLETED
처리:
  1. Ledger 생성
     - type = SALES
     - credit = 결제금액 (수익 발생)
     - reference = payment_id
출력: 전표 ID
```

#### 3-2. 환불 전표 자동 생성
```
시나리오: 환불 완료 시 환불 전표가 생긴다
트리거: Refund.status = COMPLETED
처리:
  1. Ledger 생성
     - type = REFUND
     - debit = 환불금액 (수익 차감)
     - reference = refund_id
출력: 전표 ID
```

#### 3-3. 정산 기간 마감
```
시나리오: 월말에 정산 기간을 마감한다
입력: 기간 ID
처리:
  1. 해당 기간 모든 전표 확정
  2. 기간 status = CLOSED
  3. 이후 해당 기간 전표 수정 불가
출력: 마감 결과
주의: 마감 후 수정 필요하면? → 반대 전표로 처리
```

#### 3-4. 매출 집계
```
시나리오: 특정 기간의 매출을 집계한다
입력: 시작일, 종료일
처리:
  1. Ledger에서 type=SALES인 것 합계
  2. type=REFUND인 것 차감
출력: 총매출, 환불액, 순매출
```

#### 3-5. 판매자 정산 계산 (마켓플레이스)
```
시나리오: 판매자별 정산 금액을 계산한다
입력: 정산기간 ID, 판매자 ID
처리:
  1. 해당 기간 판매자의 총 매출 집계
  2. 수수료 계산 (매출 × 수수료율)
  3. 환불 금액 차감
  4. SellerSettlement 생성
출력: 정산 금액
```

#### 3-6. 미수금 관리
```
시나리오: 후불 결제 거래처의 미수금을 추적한다
입력: 고객 ID
처리:
  1. 해당 고객의 미결제 주문 조회
  2. 연체 일수 계산
출력: 미수금 총액, 연체 목록
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 전표 생성 시점 | 결제 즉시 | 배치로 일괄 | 실시간성 vs 성능 |
| 마감 후 수정 | 반대전표 | 마감 취소 후 수정 | 감사 추적성 |
| 수수료 계산 | 건별 계산 | 기간 합산 후 계산 | 정확도 vs 성능 |
| 정산 주기 | 일정산 | 주/월 정산 | 현금 흐름 vs 운영 비용 |

### 먼저 답해야 할 질문
- [ ] 단일 판매자? 마켓플레이스? (정산 대상)
- [ ] 수수료율은 고정? 판매자별 차등?
- [ ] 세금계산서 자동 발행 필요?

---

## Phase 1 완료 조건

### 필수 구현
- [ ] 상품/창고/재고 CRUD
- [ ] 재고 예약/출고/해제 (동시성 처리 포함)
- [ ] 주문 생성 → 재고 예약 연동
- [ ] 결제 처리 (단일 결제)
- [ ] 부분 취소/환불
- [ ] 매출/환불 전표 자동 생성
- [ ] 기간별 매출 집계

### 검증 시나리오
1. 상품 A 재고 10개 → 5개 주문 → 재고 예약 5개, 가용 5개
2. 결제 완료 → 출고 → 재고 5개
3. 2개 부분 취소 → 환불 → 재고 7개
4. 동시에 10명이 마지막 1개 주문 → 1명만 성공

### 문서화 필수
- [ ] 재고 동시성 처리 방식 결정 근거
- [ ] 트랜잭션 경계 설계 문서
- [ ] 상태 전이 다이어그램 (Order, Payment)

---

## 다음 Phase와의 연결

Phase 1이 완료되면:
- **Sales**: 주문 앞단에 견적/수주 프로세스 추가
- **Procurement**: 재고 부족 시 자동 발주 연동
- **Logistics**: 결제 완료 후 출고 지시 연동 