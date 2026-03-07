# Phase 2: 영업/구매 사이클

> sales → procurement

## 목표

Phase 1에서는 "물건이 있다"고 가정하고 팔았다.
Phase 2에서는 "물건을 어디서 가져오는가"를 다룬다.

- 고객에게 팔기 전에 견적/수주 과정이 있다
- 재고가 부족하면 공급업체에서 사와야 한다

---

## 선행 조건

Phase 1 완료 필수:
- ✅ 상품/재고 관리
- ✅ 주문/결제 처리
- ✅ 정산 기본 구조

---

## 구현 순서

```
1. sales (영업/판매)      - 팔기 전 과정 (견적 → 수주)
2. procurement (구매/조달) - 물건 확보 과정 (발주 → 입고)
```

---

## 1. Sales (영업/판매)

### 왜 필요한가?
- B2B에서는 바로 주문하지 않음
- 견적 먼저, 협상, 그 다음 수주
- 고객사마다 단가가 다름
- 납기 약속이 중요

### 핵심 엔티티

```
Customer (고객/거래처)
├── id
├── name
├── business_number (사업자번호)
├── type (B2B, B2C)
├── credit_limit (신용한도)
├── credit_used (사용한도)
├── payment_terms (결제조건: 현금, 30일, 60일)
└── status (ACTIVE, SUSPENDED, CLOSED)

CustomerPrice (고객별 단가)
├── id
├── customer_id (FK)
├── product_id (FK)
├── unit_price (협의 단가)
├── discount_rate (할인율)
├── valid_from
└── valid_to

Quotation (견적서)
├── id
├── quotation_number
├── customer_id (FK)
├── sales_rep_id (담당 영업사원)
├── status (DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED)
├── valid_until (유효기간)
├── total_amount
├── notes
├── created_at
└── updated_at

QuotationItem (견적 상세)
├── id
├── quotation_id (FK)
├── product_id (FK)
├── quantity
├── unit_price
├── discount_rate
├── subtotal
└── delivery_date (희망 납기)

SalesOrder (수주)
├── id
├── order_number
├── quotation_id (FK, nullable)
├── customer_id (FK)
├── status (CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED)
├── total_amount
├── delivery_date (약속 납기)
├── created_at
└── completed_at

SalesOrderItem (수주 상세)
├── id
├── sales_order_id (FK)
├── product_id (FK)
├── quantity
├── delivered_quantity (출고된 수량)
├── remaining_quantity (잔량)
├── unit_price
└── subtotal
```

### 핵심 시나리오 (구현 순서)

#### 1-1. 고객 등록
```
시나리오: 새 거래처를 등록한다
입력: 회사명, 사업자번호, 결제조건, 신용한도
처리:
  1. 사업자번호 중복 확인
  2. Customer 생성
  3. 기본 신용한도 설정
출력: 고객 ID
```

#### 1-2. 고객별 단가 설정
```
시나리오: 특정 고객에게 특별 단가를 적용한다
입력: 고객ID, 상품ID, 단가/할인율, 유효기간
처리:
  1. 기존 단가 있으면 종료일 설정
  2. 새 CustomerPrice 생성
출력: 단가 ID
주의: 유효기간 겹침 방지
```

#### 1-3. 견적서 작성
```
시나리오: 고객에게 보낼 견적서를 작성한다
입력: 고객ID, [{상품ID, 수량, (희망납기)}]
처리:
  1. 고객별 단가 조회 (없으면 기본가)
  2. Quotation, QuotationItem 생성
  3. 유효기간 설정 (기본 7일)
  4. status = DRAFT
출력: 견적서 ID, 총액
```

#### 1-4. 견적서 발송
```
시나리오: 작성한 견적서를 고객에게 발송한다
입력: 견적서ID
처리:
  1. status = SENT
  2. (알림 모듈 연동) 이메일/알림 발송
출력: 발송 결과
```

#### 1-5. 견적 수락 → 수주 전환 ⭐
```
시나리오: 고객이 견적을 수락하여 수주로 전환한다
입력: 견적서ID, 확정 납기일
처리:
  1. 견적 유효기간 확인
  2. 고객 신용한도 확인 (총액 <= 가용한도)
  3. 재고 확인 → 납기 가능 여부 산출
  4. Quotation status = ACCEPTED
  5. SalesOrder 생성
  6. 신용한도 사용액 증가
  7. 재고 예약 (Phase 1 연동)
출력: 수주 ID
실패: 신용한도 초과, 재고 부족
```

#### 1-6. 납기 확인 (재고/생산 연동)
```
시나리오: 요청한 납기에 맞출 수 있는지 확인한다
입력: [{상품ID, 수량}], 희망납기일
처리:
  1. 현재 가용재고 확인
  2. 부족 시 → (Phase 3) 생산 일정 확인 또는 발주 리드타임 계산
  3. 가능한 납기일 산출
출력: 가능 여부, 대안 납기일
```

#### 1-7. 수주 잔량 관리
```
시나리오: 수주 중 아직 출고되지 않은 잔량을 추적한다
입력: 수주ID (또는 전체)
처리:
  1. SalesOrderItem의 remaining_quantity 집계
출력: 잔량 목록
용도: 출고 지시 시 어느 수주 건부터 처리할지 결정
```

#### 1-8. 견적 만료 처리
```
시나리오: 유효기간 지난 견적을 만료 처리한다
트리거: 스케줄러 (매일)
처리:
  1. valid_until < 오늘 && status = SENT 인 견적 조회
  2. status = EXPIRED 변경
출력: 만료 처리된 견적 수
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 견적 없이 수주 | 허용 | 불허 | B2C 지원 여부 |
| 신용한도 체크 | 수주 시 | 출고 시 | 리스크 관리 시점 |
| 수주 후 재고 예약 | 즉시 예약 | 출고 시 예약 | 재고 회전율 |
| 단가 우선순위 | 고객단가 > 기본가 | 최저가 자동 적용 | 영업 정책 |

### 먼저 답해야 할 질문
- [ ] B2C도 지원? (견적 없이 바로 주문)
- [ ] 수주 확정 시 재고 즉시 예약?
- [ ] 수주 후 단가 변경 가능?
- [ ] 부분 출고 허용?

---

## 2. Procurement (구매/조달)

### 왜 필요한가?
- 팔 물건이 없으면 사와야 함
- 안전재고 밑으로 떨어지면 발주
- 공급업체 선정, 단가 협상, 납기 관리

### 핵심 엔티티

```
Supplier (공급업체)
├── id
├── name
├── business_number
├── contact_info
├── payment_terms (결제조건)
├── lead_time_days (리드타임)
└── status (ACTIVE, SUSPENDED)

SupplierProduct (공급업체별 상품)
├── id
├── supplier_id (FK)
├── product_id (FK)
├── supplier_sku (공급업체 품번)
├── unit_price (매입가)
├── min_order_quantity (최소발주량)
├── lead_time_days (상품별 리드타임)
└── is_primary (주거래 여부)

PurchaseRequest (구매요청)
├── id
├── request_number
├── requester_id (요청자)
├── status (DRAFT, SUBMITTED, APPROVED, REJECTED)
├── reason (요청 사유)
├── created_at
└── approved_at

PurchaseRequestItem (구매요청 상세)
├── id
├── request_id (FK)
├── product_id (FK)
├── quantity
├── required_date (필요일자)
└── note

PurchaseOrder (발주서)
├── id
├── order_number
├── supplier_id (FK)
├── request_id (FK, nullable)
├── status (DRAFT, SENT, CONFIRMED, PARTIAL, RECEIVED, CANCELLED)
├── total_amount
├── expected_date (예상 입고일)
├── created_at
└── received_at

PurchaseOrderItem (발주 상세)
├── id
├── purchase_order_id (FK)
├── product_id (FK)
├── quantity
├── received_quantity (입고된 수량)
├── unit_price
└── subtotal

GoodsReceipt (입고)
├── id
├── receipt_number
├── purchase_order_id (FK)
├── warehouse_id (FK)
├── status (PENDING, INSPECTED, ACCEPTED, REJECTED)
├── received_at
└── inspected_by

GoodsReceiptItem (입고 상세)
├── id
├── receipt_id (FK)
├── po_item_id (FK)
├── quantity (입고 수량)
├── accepted_quantity (검수 합격)
├── rejected_quantity (불량)
└── reject_reason
```

### 핵심 시나리오 (구현 순서)

#### 2-1. 공급업체 등록
```
시나리오: 새 공급업체를 등록한다
입력: 업체명, 사업자번호, 결제조건, 기본 리드타임
출력: 공급업체 ID
```

#### 2-2. 공급업체별 상품/단가 등록
```
시나리오: 공급업체가 공급하는 상품과 매입가를 등록한다
입력: 공급업체ID, 상품ID, 매입가, 최소발주량, 리드타임
처리:
  1. SupplierProduct 생성
  2. 주거래 설정 (is_primary)
출력: 등록 ID
```

#### 2-3. 자동 발주 요청 (안전재고 연동) ⭐
```
시나리오: 재고가 안전재고 이하로 떨어지면 발주 요청 생성
트리거: 재고 변동 시 또는 스케줄러
처리:
  1. 가용재고 < 안전재고 인 상품 조회
  2. 발주 필요량 = 최적재고 - 현재재고 + 수주잔량
  3. PurchaseRequest 자동 생성
출력: 구매요청 ID 목록
```

#### 2-4. 구매요청 승인 (전자결재 연동 가능)
```
시나리오: 구매요청을 검토하고 승인한다
입력: 요청ID, 승인/반려, 사유
처리:
  1. 승인 시 status = APPROVED
  2. 반려 시 status = REJECTED
출력: 처리 결과
```

#### 2-5. 공급업체 선정/견적 비교
```
시나리오: 요청된 상품을 공급할 업체를 선정한다
입력: 상품ID, 수량
처리:
  1. 해당 상품 공급 가능 업체 목록 조회
  2. 단가, 리드타임, 최소발주량 비교
  3. (선택) 여러 업체에 견적 요청
출력: 업체별 비교 정보
```

#### 2-6. 발주서 생성
```
시나리오: 공급업체에 보낼 발주서를 생성한다
입력: 공급업체ID, [{상품ID, 수량}]
처리:
  1. 최소발주량 확인
  2. 단가 조회
  3. PurchaseOrder, PurchaseOrderItem 생성
  4. 예상 입고일 = 오늘 + 리드타임
출력: 발주서 ID, 총액, 예상 입고일
```

#### 2-7. 발주서 발송
```
시나리오: 발주서를 공급업체에 발송한다
입력: 발주서ID
처리:
  1. status = SENT
  2. (알림) 공급업체에 이메일/팩스 발송
출력: 발송 결과
```

#### 2-8. 입고 등록 ⭐
```
시나리오: 물건이 도착하여 입고 처리한다
입력: 발주서ID, 창고ID, [{상품ID, 입고수량}]
처리:
  1. GoodsReceipt 생성
  2. PurchaseOrderItem.received_quantity 증가
  3. 전량 입고 시 PO status = RECEIVED
  4. 부분 입고 시 PO status = PARTIAL
출력: 입고 ID
주의: 아직 재고 반영 안 함 (검수 후 반영)
```

#### 2-9. 입고 검수 ⭐
```
시나리오: 입고된 물건을 검수한다
입력: 입고ID, [{품목, 합격수량, 불량수량, 불량사유}]
처리:
  1. GoodsReceiptItem 업데이트
  2. 합격 수량만 재고 반영 (inventory.receive)
  3. 불량 있으면 반품/클레임 처리 대기
출력: 검수 결과
```

#### 2-10. 반품/클레임 처리
```
시나리오: 불량 상품을 공급업체에 반품한다
입력: 입고ID, [{품목, 수량, 사유}]
처리:
  1. 반품 전표 생성
  2. 공급업체에 알림
  3. 대체품 또는 환불 처리
출력: 반품 ID
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 발주 자동화 | 완전 자동 | 승인 후 발주 | 통제 수준 |
| 입고 즉시 재고 반영 | 즉시 | 검수 후 | 품질 관리 수준 |
| 부분 입고 | 허용 | 전량만 | 공급업체 협약 |
| 단일 vs 복수 공급업체 | 단일 | 복수 분산 | 리스크 분산 |

### 먼저 답해야 할 질문
- [ ] 안전재고 도달 시 자동 발주?
- [ ] 발주 전 승인 필요?
- [ ] 입고 검수 프로세스 있음?
- [ ] 공급업체 평가 시스템 필요?

---

## Phase 2 완료 조건

### 필수 구현
- [ ] 고객/공급업체 관리
- [ ] 고객별/공급업체별 단가 관리
- [ ] 견적 → 수주 프로세스
- [ ] 신용한도 체크
- [ ] 구매요청 → 발주 프로세스
- [ ] 입고/검수 → 재고 반영 연동

### 검증 시나리오
1. 고객 A에게 견적 발송 → 수락 → 수주 생성 → 재고 예약
2. 재고 부족 알림 → 구매요청 → 발주 → 입고 → 검수 → 재고 증가
3. 수주 납기에 맞춰 발주 리드타임 역산
4. 신용한도 초과 고객의 수주 시도 → 거절

### 문서화 필수
- [ ] 견적 → 수주 → 주문 관계 정리
- [ ] 발주 리드타임 계산 로직
- [ ] 신용한도 관리 정책

---

## 다음 Phase와의 연결

Phase 2가 완료되면:
- **Logistics**: 수주 확정 → 출고 지시 자동 생성
- **Production**: 재고 부족 & 자체 생산 시 생산 지시 연동
- **Settlement**: 매입 전표 자동 생성, 공급업체 정산