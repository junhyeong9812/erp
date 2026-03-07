# Phase 3: 물류/생산

> logistics → production

## 목표

Phase 1-2에서 주문받고 물건 확보하는 것까지 했다.
Phase 3에서는 물건을 "보내고" "만드는" 것을 다룬다.

- 결제 완료된 주문을 출고하고 배송한다
- 물건이 없으면 직접 만든다 (제조업)

---

## 선행 조건

Phase 1-2 완료 필수:
- ✅ 재고 예약/출고
- ✅ 주문/결제
- ✅ 수주/발주

---

## 구현 순서

```
1. logistics (물류/배송) - 물건을 보낸다
2. production (생산/제조) - 물건을 만든다
```

---

## 1. Logistics (물류/배송)

### 왜 필요한가?
- 결제 받았으면 물건을 보내야 함
- 창고에서 피킹 → 포장 → 배송기사 배정 → 배송
- 배송 추적, 반품 수거까지

### 핵심 엔티티

```
ShippingOrder (출고지시)
├── id
├── order_number
├── sales_order_id (FK) / order_id (FK)
├── warehouse_id (FK)
├── status (PENDING, PICKING, PACKED, SHIPPED, DELIVERED, CANCELLED)
├── priority (HIGH, NORMAL, LOW)
├── requested_date (요청일)
├── shipped_date (출고일)
└── created_at

ShippingOrderItem (출고 상세)
├── id
├── shipping_order_id (FK)
├── product_id (FK)
├── quantity
├── picked_quantity (피킹된 수량)
├── location (창고 내 위치)
└── status (PENDING, PICKED, PACKED)

PickingList (피킹리스트)
├── id
├── list_number
├── warehouse_id (FK)
├── picker_id (작업자)
├── status (CREATED, IN_PROGRESS, COMPLETED)
├── created_at
└── completed_at

PickingListItem (피킹 상세)
├── id
├── picking_list_id (FK)
├── shipping_order_item_id (FK)
├── location (선반 위치)
├── quantity
├── picked_quantity
└── status (PENDING, PICKED, SHORT) // SHORT = 재고 부족

Shipment (배송)
├── id
├── shipment_number
├── shipping_order_id (FK)
├── carrier_id (배송업체 FK)
├── driver_id (배송기사 FK)
├── tracking_number (송장번호)
├── status (CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED)
├── recipient_name
├── recipient_address
├── recipient_phone
├── estimated_date (예상 배송일)
├── delivered_date
└── created_at

Carrier (배송업체)
├── id
├── name (CJ대한통운, 롯데택배...)
├── api_endpoint (연동 URL)
├── is_active
└── config (API 설정)

Driver (배송기사) - 자체 배송 시
├── id
├── name
├── phone
├── vehicle_number
├── zone_id (담당 권역)
├── status (AVAILABLE, ON_DELIVERY, OFF_DUTY)
└── current_capacity (현재 적재량)

DeliveryZone (배송권역)
├── id
├── name (강남권, 강북권...)
├── postal_codes (우편번호 목록)
└── default_driver_id

ReturnRequest (반품요청)
├── id
├── request_number
├── order_id (FK)
├── reason (DEFECT, WRONG_ITEM, CHANGE_MIND, DAMAGED)
├── status (REQUESTED, APPROVED, PICKUP_SCHEDULED, PICKED_UP, RECEIVED, REFUNDED, REJECTED)
├── pickup_date
├── received_date
└── created_at

ReturnRequestItem (반품 상세)
├── id
├── return_request_id (FK)
├── product_id (FK)
├── quantity
├── condition (GOOD, DAMAGED, DEFECTIVE)
└── restock (재입고 여부)
```

### 핵심 시나리오 (구현 순서)

#### 1-1. 출고지시 생성
```
시나리오: 결제 완료된 주문에 대해 출고 지시를 생성한다
트리거: Order.status = PAID (또는 수동)
처리:
  1. 출고 창고 결정 (재고 있는 곳, 또는 고객 근처)
  2. ShippingOrder 생성
  3. ShippingOrderItem 생성 (재고 위치 포함)
  4. 우선순위 설정 (급배송 등)
출력: 출고지시 ID
```

#### 1-2. 피킹리스트 생성 ⭐
```
시나리오: 여러 출고지시를 묶어 피킹리스트를 만든다
입력: 창고ID, 출고지시 ID 목록 (또는 자동 그룹핑)
처리:
  1. 동일 창고 출고지시 묶음
  2. 위치별로 정렬 (동선 최적화)
  3. PickingList 생성
  4. 작업자 배정
출력: 피킹리스트 ID
최적화: 위치 기준 정렬로 창고 동선 최소화
```

#### 1-3. 피킹 처리
```
시나리오: 작업자가 물건을 픽업한다
입력: 피킹리스트ID, [{품목, 피킹수량}]
처리:
  1. PickingListItem.picked_quantity 업데이트
  2. 재고 부족 시 status = SHORT 표시
  3. 전체 완료 시 PickingList.status = COMPLETED
  4. 재고 출고 처리 (inventory.release)
출력: 피킹 결과
주의: 피킹 중 재고 부족 발생 시 처리 방안
```

#### 1-4. 포장 처리
```
시나리오: 피킹된 물건을 포장한다
입력: 출고지시ID, 포장 정보 (박스 수, 무게)
처리:
  1. ShippingOrder.status = PACKED
  2. 송장 출력 준비
출력: 포장 완료
```

#### 1-5. 배송업체 연동/송장 발급
```
시나리오: 외부 택배사 API로 송장을 발급받는다
입력: 출고지시ID, 배송업체ID
처리:
  1. 배송업체 API 호출
  2. tracking_number 수신
  3. Shipment 생성
출력: 송장번호
```

#### 1-6. 자체 배송 - 배차 ⭐
```
시나리오: 자체 배송 시 배송기사를 배정한다
입력: 출고지시ID 목록, 배송일
처리:
  1. 배송지 주소로 권역 판단
  2. 해당 권역 기사 조회 (available && 적재 여유)
  3. 기사별 배송 건 배정
  4. 배송 순서 최적화 (선택)
출력: 배차 결과
알고리즘: Round-Robin + 우선순위 or 적재량 기반
```

#### 1-7. 배송 상태 추적
```
시나리오: 배송 상태를 업데이트한다
입력: 송장번호, 상태, (위치)
처리:
  1. Shipment.status 업데이트
  2. 배송완료 시:
     - Order.status = COMPLETED
     - (알림) 고객에게 배송완료 알림
출력: 상태 변경 결과
소스: 외부 API 웹훅 또는 기사 앱
```

#### 1-8. 반품 요청 접수
```
시나리오: 고객이 반품을 요청한다
입력: 주문ID, [{상품ID, 수량, 사유}]
처리:
  1. 반품 가능 여부 확인 (기간, 상품 종류)
  2. ReturnRequest 생성
  3. 승인 필요 시 status = REQUESTED
  4. 자동 승인 시 status = APPROVED
출력: 반품요청 ID
```

#### 1-9. 반품 수거 배차
```
시나리오: 승인된 반품을 수거한다
입력: 반품요청ID, 수거일
처리:
  1. 수거 배차 (배송과 유사)
  2. status = PICKUP_SCHEDULED
출력: 수거 정보
```

#### 1-10. 반품 입고/검수
```
시나리오: 수거된 반품을 검수하고 처리한다
입력: 반품요청ID, [{상품ID, 상태, 재입고여부}]
처리:
  1. 상품 상태 확인
  2. 재입고 가능 → 재고 반영
  3. 불가 → 폐기 또는 별도 처리
  4. 환불 처리 트리거 (payment 연동)
출력: 검수 결과
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 출고지시 생성 | 결제 즉시 자동 | 수동 생성 | 자동화 수준 |
| 피킹 단위 | 주문별 | 다건 묶음(Wave) | 물량, 효율 |
| 배송 | 외부 택배 | 자체 배송 | 비용 vs 통제 |
| 반품 승인 | 자동 | 검토 후 수동 | CS 정책 |

### 먼저 답해야 할 질문
- [ ] 자체 배송? 택배사 연동?
- [ ] 피킹리스트 단위는? (건별 vs 묶음)
- [ ] 반품 자동 승인 범위?
- [ ] 부분 출고 허용?

---

## 2. Production (생산/제조)

### 왜 필요한가?
- 물건을 직접 만드는 제조업 시나리오
- 부품(원자재)으로 완제품을 조립
- 재고 부족 시 생산으로 해결

### 핵심 엔티티

```
BOM (Bill of Materials - 자재명세서)
├── id
├── product_id (완제품 FK)
├── version
├── status (ACTIVE, DEPRECATED)
└── created_at

BOMItem (BOM 상세)
├── id
├── bom_id (FK)
├── component_id (부품/원자재 FK)
├── quantity (완제품 1개당 필요량)
├── unit
└── sequence (조립 순서)

ProductionPlan (생산계획)
├── id
├── plan_number
├── period_start
├── period_end
├── status (DRAFT, APPROVED, IN_PROGRESS, COMPLETED)
└── created_at

ProductionPlanItem (생산계획 상세)
├── id
├── plan_id (FK)
├── product_id (FK)
├── quantity (계획 수량)
├── produced_quantity (생산 완료 수량)
└── required_date

WorkOrder (작업지시)
├── id
├── order_number
├── plan_item_id (FK, nullable)
├── product_id (FK)
├── bom_id (FK)
├── quantity
├── status (CREATED, MATERIAL_ISSUED, IN_PROGRESS, COMPLETED, CANCELLED)
├── line_id (생산라인)
├── scheduled_date
├── started_at
├── completed_at
└── created_at

MaterialIssue (자재출고)
├── id
├── issue_number
├── work_order_id (FK)
├── status (REQUESTED, ISSUED, PARTIAL)
└── issued_at

MaterialIssueItem (자재출고 상세)
├── id
├── material_issue_id (FK)
├── component_id (FK)
├── required_quantity (BOM 기준 필요량)
├── issued_quantity (실제 출고량)
└── warehouse_id

ProductionResult (생산실적)
├── id
├── work_order_id (FK)
├── produced_quantity (양품)
├── defect_quantity (불량)
├── defect_reason
├── worker_id
├── produced_at
└── created_at

ProductionLine (생산라인)
├── id
├── name
├── capacity_per_day (일일 생산능력)
├── status (ACTIVE, MAINTENANCE, INACTIVE)
└── products (생산 가능 품목)
```

### 핵심 시나리오 (구현 순서)

#### 2-1. BOM 등록
```
시나리오: 완제품의 자재명세서를 등록한다
입력: 완제품ID, [{부품ID, 수량, 순서}]
처리:
  1. BOM 생성
  2. BOMItem 목록 생성
  3. 기존 BOM 있으면 version 관리
출력: BOM ID
예시: 노트북 = CPU x1 + RAM x2 + SSD x1 + 케이스 x1
```

#### 2-2. 생산 가능 여부 확인 (MRP 기초)
```
시나리오: 특정 수량 생산에 필요한 자재가 있는지 확인
입력: 완제품ID, 생산수량
처리:
  1. BOM 조회
  2. 부품별 필요량 계산 (BOM수량 × 생산수량)
  3. 현재 재고와 비교
  4. 부족 부품 목록 산출
출력: 생산 가능 여부, 부족 자재 목록
```

#### 2-3. 생산계획 수립
```
시나리오: 주간/월간 생산계획을 수립한다
입력: 기간, [{완제품ID, 수량, 필요일자}]
처리:
  1. ProductionPlan 생성
  2. ProductionPlanItem 생성
  3. 자재 소요량 집계 (MRP)
  4. 부족 자재 → 구매요청 연동 (Phase 2)
출력: 생산계획 ID, 자재 소요 목록
```

#### 2-4. 작업지시 생성
```
시나리오: 생산계획을 기반으로 작업지시를 생성한다
입력: 계획항목ID (또는 직접: 완제품ID, 수량)
처리:
  1. BOM 확인
  2. 생산라인 배정
  3. WorkOrder 생성
  4. 일정 산출 (라인 능력 기준)
출력: 작업지시 ID, 예상 완료일
```

#### 2-5. 자재 출고 (생산투입) ⭐
```
시나리오: 작업지시에 필요한 자재를 출고한다
입력: 작업지시ID
처리:
  1. BOM 기준 필요 자재 목록 산출
  2. 창고에서 자재 출고 (inventory.release)
  3. MaterialIssue, MaterialIssueItem 생성
  4. WorkOrder.status = MATERIAL_ISSUED
출력: 자재출고 ID
주의: 부족 시 부분 출고? 대기?
```

#### 2-6. 생산 시작
```
시나리오: 자재 출고 후 생산을 시작한다
입력: 작업지시ID
처리:
  1. 자재 출고 완료 확인
  2. WorkOrder.status = IN_PROGRESS
  3. started_at 기록
출력: 시작 완료
```

#### 2-7. 생산실적 등록 ⭐
```
시나리오: 생산 완료된 수량을 등록한다
입력: 작업지시ID, 양품수량, 불량수량, (불량사유)
처리:
  1. ProductionResult 생성
  2. 양품 → 완제품 재고 입고 (inventory.receive)
  3. 불량 → 불량 창고 또는 폐기
  4. 목표수량 도달 시 WorkOrder.status = COMPLETED
출력: 생산실적 ID
```

#### 2-8. 불량 분석
```
시나리오: 불량 현황을 분석한다
입력: 기간, (라인ID), (제품ID)
처리:
  1. ProductionResult 집계
  2. 불량률 = 불량수량 / (양품 + 불량)
  3. 불량 사유별 분류
출력: 불량률, 사유별 통계
```

#### 2-9. 생산계획 대비 실적
```
시나리오: 계획 대비 실제 생산 현황을 조회한다
입력: 계획ID
처리:
  1. ProductionPlanItem.quantity vs produced_quantity
  2. 달성률 계산
출력: 품목별 달성률
```

### 트레이드오프 포인트

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 자재 부족 시 | 대기 (생산 중단) | 부분 생산 | 긴급도 |
| BOM 변경 | 신규 버전 | 기존 수정 | 이력 관리 필요성 |
| 불량 처리 | 즉시 폐기 | 재작업 가능 | 제품 특성 |
| 생산실적 단위 | 실시간 | 일괄 (일말) | 현장 환경 |

### 먼저 답해야 할 질문
- [ ] 제조업 시나리오 필요한가? (유통만이면 스킵)
- [ ] BOM 계층 구조 필요? (반제품 → 완제품)
- [ ] 생산라인 개념 필요?
- [ ] 불량 재작업 프로세스?

---

## Phase 3 완료 조건

### 필수 구현 (Logistics)
- [ ] 출고지시 생성 (주문 연동)
- [ ] 피킹/패킹 프로세스
- [ ] 배송 상태 관리
- [ ] 반품 요청/처리

### 필수 구현 (Production) - 제조업인 경우
- [ ] BOM 관리
- [ ] 작업지시 생성
- [ ] 자재 출고 (재고 연동)
- [ ] 생산실적 등록 (재고 입고)

### 검증 시나리오
1. 주문 결제 완료 → 출고지시 자동 생성 → 피킹 → 배송
2. 배송완료 → 주문 상태 완료
3. 반품 요청 → 수거 → 검수 → 재입고 또는 폐기 → 환불
4. (제조) 재고 부족 → 생산계획 → 자재 출고 → 생산 → 완제품 입고

### 문서화 필수
- [ ] 출고 → 배송 상태 전이도
- [ ] 피킹리스트 생성 로직 (동선 최적화)
- [ ] BOM 구조 설계 (제조 시)
- [ ] 생산-재고 연동 흐름

---

## 다음 Phase와의 연결

Phase 3가 완료되면:
- **HR**: 배송기사 근태, 생산직 근무 관리
- **Approval**: 반품 승인, 생산계획 승인 결재
- **Settlement**: 배송비 정산, 생산원가 계산