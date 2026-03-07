# Phase 5: 부가 기능

> crm → promotion → notification → report

## 목표

Phase 1-4가 ERP의 "뼈대"였다면,
Phase 5는 "살"을 붙이는 것이다.

- 고객을 더 잘 관리하고 (CRM)
- 마케팅으로 매출을 올리고 (Promotion)
- 적시에 알림을 보내고 (Notification)
- 데이터로 의사결정한다 (Report)

---

## 선행 조건

Phase 1-4 완료 필수:
- ✅ 고객/주문/결제 (CRM의 기반)
- ✅ 가격/할인 적용 구조 (Promotion의 기반)
- ✅ 이벤트 발생 지점 (Notification의 트리거)
- ✅ 축적된 데이터 (Report의 소스)

---

## 구현 순서

```
1. crm (고객관리)        - 고객을 더 잘 안다
2. promotion (프로모션)  - 고객을 더 사게 한다
3. notification (알림)   - 적시에 알려준다
4. report (리포트)       - 데이터로 본다
```

---

## 1. CRM (고객관리)

### 왜 필요한가?
- 고객은 그냥 주문하는 사람이 아님
- 관계를 쌓고, 이력을 관리하고, 문제를 해결해야 함
- 좋은 고객은 유지하고, 이탈 고객은 복귀시킨다

### 핵심 엔티티

```
CustomerProfile (고객 프로필 확장)
├── id
├── customer_id (FK)
├── grade (VIP, GOLD, SILVER, BRONZE, NORMAL)
├── grade_updated_at
├── total_purchase_amount (누적 구매액)
├── total_order_count (누적 주문 수)
├── first_purchase_date
├── last_purchase_date
├── average_order_value
└── churn_risk (이탈 위험도)

CustomerGradePolicy (등급 정책)
├── id
├── grade
├── min_purchase_amount (최소 누적 구매액)
├── min_order_count (최소 주문 수)
├── benefits (혜택 설명)
├── discount_rate (등급별 할인율)
└── point_rate (적립률)

ContactHistory (상담 이력)
├── id
├── customer_id (FK)
├── channel (PHONE, EMAIL, CHAT, VISIT)
├── type (INQUIRY, COMPLAINT, REQUEST, FEEDBACK)
├── subject
├── content
├── handler_id (담당자 FK)
├── status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
├── resolution
├── created_at
└── resolved_at

Claim (클레임/불만)
├── id
├── customer_id (FK)
├── order_id (FK, nullable)
├── type (PRODUCT_DEFECT, DELIVERY_ISSUE, SERVICE, OTHER)
├── severity (LOW, MEDIUM, HIGH, CRITICAL)
├── description
├── status (RECEIVED, INVESTIGATING, RESOLVED, CLOSED)
├── compensation (보상 내용)
├── handler_id
├── created_at
└── resolved_at

CustomerNote (고객 메모)
├── id
├── customer_id (FK)
├── author_id (FK)
├── content
├── is_important
└── created_at

SalesActivity (영업 활동)
├── id
├── customer_id (FK)
├── sales_rep_id (FK)
├── type (CALL, MEETING, EMAIL, DEMO)
├── subject
├── result
├── next_action
├── scheduled_at
└── completed_at
```

### 핵심 시나리오

#### 1-1. 고객 등급 산정 ⭐
```
시나리오: 구매 실적에 따라 고객 등급을 산정한다
트리거: 주문 완료 시 또는 월간 배치
처리:
  1. 최근 12개월 구매액/주문수 집계
  2. CustomerGradePolicy와 비교
  3. 등급 상향/유지/하향 결정
  4. CustomerProfile 업데이트
출력: 변경된 등급
혜택: 등급별 할인율, 적립률, 무료배송 등
```

#### 1-2. 상담 이력 등록
```
시나리오: 고객 문의를 접수하고 이력을 남긴다
입력: 고객ID, 채널, 유형, 내용
처리:
  1. ContactHistory 생성
  2. 담당자 배정 (자동 또는 수동)
  3. status = OPEN
출력: 상담 ID
```

#### 1-3. 클레임 처리
```
시나리오: 고객 불만을 접수하고 해결한다
입력: 고객ID, (주문ID), 유형, 심각도, 내용
처리:
  1. Claim 생성
  2. 심각도에 따라 에스컬레이션
  3. 조사 → 해결책 결정 → 보상
  4. status 업데이트
출력: 처리 결과
보상: 포인트 지급, 재배송, 환불 등
```

#### 1-4. 이탈 위험 고객 감지
```
시나리오: 구매가 뜸해진 고객을 감지한다
트리거: 일간/주간 배치
처리:
  1. 최근 구매일 기준 경과일 계산
  2. 평균 구매 주기 대비 이상치 감지
  3. churn_risk 점수 산정
출력: 위험 고객 목록
후속: 재구매 유도 캠페인 (promotion 연동)
```

#### 1-5. 고객 360도 뷰
```
시나리오: 고객의 모든 정보를 한눈에 본다
입력: 고객ID
출력:
  - 기본 정보 + 등급
  - 주문 이력 (최근 N건)
  - 상담/클레임 이력
  - 포인트/쿠폰 현황
  - 담당 영업사원 메모
```

---

## 2. Promotion (포인트/쿠폰/프로모션)

### 왜 필요한가?
- 고객이 더 사게 하려면 인센티브가 필요
- 포인트 적립, 쿠폰 할인, 기간 한정 프로모션
- 복잡한 조건과 정합성 관리

### 핵심 엔티티

```
PointPolicy (포인트 정책)
├── id
├── name
├── earn_rate (적립률, 기본 1%)
├── earn_condition (적립 조건)
├── expiry_months (유효기간, 12개월)
├── min_use_amount (최소 사용 금액)
├── max_use_rate (최대 사용 비율, 예: 결제액의 10%)
└── status (ACTIVE, INACTIVE)

PointBalance (포인트 잔액)
├── id
├── customer_id (FK)
├── total_earned (총 적립)
├── total_used (총 사용)
├── total_expired (총 소멸)
├── available (가용 잔액)
└── updated_at

PointTransaction (포인트 거래)
├── id
├── customer_id (FK)
├── type (EARN, USE, EXPIRE, CANCEL, ADJUST)
├── amount
├── balance_after
├── reference_type (ORDER, REVIEW, EVENT, MANUAL)
├── reference_id
├── description
├── expires_at
└── created_at

CouponTemplate (쿠폰 템플릿)
├── id
├── name
├── code_prefix
├── discount_type (FIXED, PERCENT)
├── discount_value (1000원 또는 10%)
├── max_discount (최대 할인액)
├── min_order_amount (최소 주문 금액)
├── applicable_products (적용 상품, null이면 전체)
├── applicable_categories (적용 카테고리)
├── valid_days (발급 후 유효일)
├── max_issue_count (최대 발급 수)
├── issued_count (발급된 수)
└── status (ACTIVE, INACTIVE, DEPLETED)

Coupon (쿠폰 - 발급된 개별 쿠폰)
├── id
├── template_id (FK)
├── code (고유 코드)
├── customer_id (FK)
├── status (ISSUED, USED, EXPIRED, CANCELLED)
├── issued_at
├── expires_at
├── used_at
└── used_order_id

Promotion (프로모션/이벤트)
├── id
├── name
├── type (DISCOUNT, GIFT, BUNDLE, POINT_BOOST)
├── start_date
├── end_date
├── conditions (JSON: 적용 조건)
├── benefits (JSON: 혜택 내용)
├── priority (우선순위)
├── stackable (중복 적용 가능 여부)
├── status (SCHEDULED, ACTIVE, ENDED)
└── created_at

PromotionUsage (프로모션 사용 이력)
├── id
├── promotion_id (FK)
├── order_id (FK)
├── customer_id (FK)
├── discount_amount
└── applied_at
```

### 핵심 시나리오

#### 2-1. 포인트 적립 ⭐
```
시나리오: 주문 완료 시 포인트를 적립한다
트리거: Order.status = COMPLETED (배송완료 후)
처리:
  1. 적립 정책 조회 (등급별 적립률)
  2. 적립액 계산 = 결제금액 × 적립률
  3. PointTransaction 생성 (type=EARN)
  4. PointBalance.available 증가
  5. 만료일 설정 (적립일 + 12개월)
출력: 적립 포인트
주의: 취소된 주문에 대한 적립 취소 필요
```

#### 2-2. 포인트 사용 ⭐
```
시나리오: 결제 시 포인트를 사용한다
입력: 주문ID, 사용 포인트
처리:
  1. 가용 잔액 확인
  2. 최대 사용 가능액 계산 (주문액의 10%)
  3. PointTransaction 생성 (type=USE)
  4. PointBalance.available 감소
  5. 주문 결제금액에서 차감
출력: 사용 결과
FIFO: 먼저 적립된 포인트부터 사용 (만료일 기준)
```

#### 2-3. 포인트 소멸 처리
```
시나리오: 유효기간 지난 포인트를 소멸시킨다
트리거: 일간 배치
처리:
  1. expires_at < 오늘 && 미사용 포인트 조회
  2. PointTransaction 생성 (type=EXPIRE)
  3. PointBalance 업데이트
출력: 소멸된 포인트 합계
알림: 소멸 예정 포인트 사전 알림 (7일 전)
```

#### 2-4. 쿠폰 발급
```
시나리오: 고객에게 쿠폰을 발급한다
입력: 템플릿ID, 고객ID (또는 대량 발급)
처리:
  1. 발급 가능 여부 확인 (max_issue_count)
  2. 고유 코드 생성
  3. Coupon 생성
  4. issued_count 증가
출력: 쿠폰 코드
발급 방법: 자동(가입 시), 수동, 이벤트
```

#### 2-5. 쿠폰 사용 ⭐
```
시나리오: 결제 시 쿠폰을 적용한다
입력: 쿠폰코드, 주문ID
처리:
  1. 쿠폰 유효성 확인:
     - 존재 여부
     - 본인 쿠폰인지
     - 만료 여부
     - 이미 사용 여부
  2. 적용 조건 확인:
     - 최소 주문금액
     - 적용 가능 상품/카테고리
  3. 할인액 계산
  4. Coupon.status = USED
  5. 주문에 할인 적용
출력: 할인 금액
```

#### 2-6. 프로모션 적용 (복잡) ⭐⭐
```
시나리오: 주문에 적용 가능한 프로모션을 찾아 적용한다
입력: 장바구니 정보
처리:
  1. 현재 활성 프로모션 목록 조회
  2. 각 프로모션 적용 조건 체크:
     - 기간
     - 대상 상품/카테고리
     - 최소 금액
     - 고객 조건 (첫 구매, 등급 등)
  3. 적용 가능한 프로모션 목록 반환
  4. 중복 적용 규칙 체크:
     - stackable=true면 중복 가능
     - 아니면 우선순위 높은 것만
  5. 최종 할인액 계산
출력: 적용된 프로모션, 총 할인액
```

#### 2-7. 부분취소 시 처리 ⭐⭐
```
시나리오: 포인트/쿠폰 사용 주문이 부분 취소된다
입력: 주문ID, 취소 항목
처리:
  1. 취소 비율 계산
  2. 포인트 사용분 비례 환불:
     - 포인트로 돌려줄지, 현금 환불할지?
     - 이미 적립된 포인트 회수
  3. 쿠폰:
     - 부분취소 후에도 조건 충족? → 유지
     - 조건 미달? → 쿠폰 복원 또는 차액 청구
출력: 처리 결과
복잡도: 매우 높음, 정책 결정 필요
```

### 트레이드오프

| 상황 | 선택지 A | 선택지 B | 결정 기준 |
|------|----------|----------|-----------|
| 포인트 적립 시점 | 결제 시 | 배송완료 시 | 취소율 |
| 부분취소 포인트 | 포인트 환불 | 현금 환불 | 재구매 유도 |
| 쿠폰 중복 | 1개만 | 복수 허용 | 마케팅 정책 |
| 프로모션 중복 | 불가 | 조건부 허용 | 복잡도 |

---

## 3. Notification (알림)

### 왜 필요한가?
- 사용자는 앱을 계속 보고 있지 않음
- 중요한 일이 생기면 알려줘야 함
- 채널별로 다르게, 개인 설정 존중

### 핵심 엔티티

```
NotificationTemplate (알림 템플릿)
├── id
├── code (ORDER_CONFIRMED, PAYMENT_COMPLETE, DELIVERY_START...)
├── name
├── channels (EMAIL, SMS, PUSH, KAKAO)
├── title_template ("주문이 확인되었습니다")
├── body_template ("주문번호 {{orderNumber}}...")
├── variables (사용 가능 변수 목록)
└── status

NotificationSetting (사용자 설정)
├── id
├── user_id (FK)
├── channel (EMAIL, SMS, PUSH)
├── enabled (수신 여부)
├── quiet_hours_start (방해금지 시작)
├── quiet_hours_end (방해금지 종료)
└── categories (수신할 카테고리)

Notification (발송된 알림)
├── id
├── user_id (FK)
├── template_id (FK)
├── channel
├── title
├── body
├── data (추가 데이터, JSON)
├── status (PENDING, SENT, FAILED, READ)
├── sent_at
├── read_at
├── error_message
└── created_at

NotificationQueue (발송 대기열)
├── id
├── notification_id (FK)
├── scheduled_at
├── attempts
├── last_attempt_at
├── status (QUEUED, PROCESSING, COMPLETED, FAILED)
└── created_at
```

### 핵심 시나리오

#### 3-1. 알림 발송 요청
```
시나리오: 이벤트 발생 시 알림을 발송한다
트리거: 주문확인, 결제완료, 배송시작, 결재요청 등
처리:
  1. 이벤트 감지 (EventListener)
  2. 템플릿 조회
  3. 수신자 설정 확인 (채널별 수신 여부)
  4. 변수 치환 ({{orderNumber}} → 실제 값)
  5. Notification 생성
  6. NotificationQueue에 등록
출력: 알림 ID
```

#### 3-2. 실제 발송 처리
```
시나리오: 대기열의 알림을 실제로 발송한다
트리거: 스케줄러 (매 분)
처리:
  1. QUEUED 상태인 것 조회
  2. 채널별 발송:
     - EMAIL: SMTP or 외부 서비스 (SendGrid)
     - SMS: 외부 서비스 (Twilio, 알리고)
     - PUSH: FCM/APNs
     - KAKAO: 카카오 알림톡 API
  3. 성공/실패 상태 업데이트
  4. 실패 시 재시도 (최대 3회)
출력: 발송 결과
```

#### 3-3. 알림 설정 변경
```
시나리오: 사용자가 알림 수신 설정을 변경한다
입력: 사용자ID, 채널, 설정값
처리:
  1. NotificationSetting 조회/생성
  2. 설정 업데이트
출력: 변경된 설정
```

#### 3-4. 예약 발송
```
시나리오: 특정 시간에 알림을 발송한다
입력: 알림내용, 예약시간
처리:
  1. Notification 생성
  2. NotificationQueue.scheduled_at = 예약시간
  3. 해당 시간에 발송
출력: 예약 ID
용도: 마케팅 캠페인, 리마인더
```

### 알림 트리거 목록

| 이벤트 | 알림 내용 | 채널 |
|--------|----------|------|
| 주문 확인 | 주문이 접수되었습니다 | 이메일, 푸시 |
| 결제 완료 | 결제가 완료되었습니다 | 이메일, 푸시, 카카오 |
| 배송 시작 | 상품이 발송되었습니다 | 푸시, 카카오 |
| 배송 완료 | 상품이 도착했습니다 | 푸시 |
| 결재 요청 | 결재 요청이 있습니다 | 이메일, 푸시 |
| 휴가 승인 | 휴가가 승인되었습니다 | 푸시 |
| 재고 부족 | 재고가 부족합니다 | 이메일 (담당자) |
| 포인트 소멸 예정 | 7일 후 포인트가 소멸됩니다 | 푸시, 카카오 |

---

## 4. Report (리포트/대시보드)

### 왜 필요한가?
- 데이터가 쌓여도 보지 않으면 의미 없음
- 의사결정에 필요한 정보를 한눈에
- 실시간 모니터링 + 정기 리포트

### 핵심 엔티티

```
ReportDefinition (리포트 정의)
├── id
├── name
├── category (SALES, INVENTORY, HR, FINANCE)
├── query (SQL or 집계 로직)
├── parameters (필터 파라미터)
├── columns (컬럼 정의)
├── chart_type (BAR, LINE, PIE, TABLE)
└── status

ReportSchedule (정기 리포트)
├── id
├── report_id (FK)
├── frequency (DAILY, WEEKLY, MONTHLY)
├── day_of_week / day_of_month
├── time
├── recipients (수신자 목록)
├── format (PDF, EXCEL, EMAIL)
└── status

ReportSnapshot (리포트 스냅샷)
├── id
├── report_id (FK)
├── generated_at
├── parameters_used
├── data (JSON)
├── file_path (생성된 파일)
└── created_by

Dashboard (대시보드)
├── id
├── name
├── owner_id (FK)
├── is_public
├── layout (위젯 배치, JSON)
└── created_at

DashboardWidget (대시보드 위젯)
├── id
├── dashboard_id (FK)
├── name
├── type (METRIC, CHART, TABLE, ALERT)
├── data_source (리포트 ID or 직접 쿼리)
├── config (설정, JSON)
├── position (x, y, width, height)
└── refresh_interval
```

### 핵심 리포트 목록

#### 영업/매출
| 리포트 | 지표 | 주기 |
|--------|------|------|
| 일별 매출 현황 | 주문건수, 매출액, 객단가 | 일간 |
| 상품별 판매 순위 | 판매량, 매출액, 이익률 | 주간 |
| 고객별 매출 | 고객사별 매출, 성장률 | 월간 |
| 채널별 매출 | 온라인/오프라인, 플랫폼별 | 월간 |

#### 재고
| 리포트 | 지표 | 주기 |
|--------|------|------|
| 재고 현황 | 품목별 수량, 금액, 회전율 | 실시간 |
| 재고 부족 알림 | 안전재고 미달 품목 | 실시간 |
| 입출고 현황 | 입고/출고량, 조정 내역 | 일간 |
| 재고 실사 리포트 | 차이 분석 | 분기 |

#### 물류
| 리포트 | 지표 | 주기 |
|--------|------|------|
| 배송 현황 | 출고건수, 배송완료율, 평균소요일 | 일간 |
| 반품 현황 | 반품건수, 사유별 분류 | 주간 |
| 배송기사별 실적 | 배송건수, 완료율 | 월간 |

#### 재무
| 리포트 | 지표 | 주기 |
|--------|------|------|
| 손익 현황 | 매출, 매출원가, 판관비, 영업이익 | 월간 |
| 미수금 현황 | 거래처별 미수금, 연체 | 주간 |
| 정산 현황 | 판매자 정산액, 지급 현황 | 월간 |

#### 인사
| 리포트 | 지표 | 주기 |
|--------|------|------|
| 근태 현황 | 지각/조퇴/결근, 초과근무 | 월간 |
| 급여 현황 | 부서별 인건비, 평균 급여 | 월간 |
| 휴가 사용 현황 | 부서별 휴가 사용률 | 분기 |

### 핵심 시나리오

#### 4-1. 실시간 대시보드 조회
```
시나리오: 오늘의 핵심 지표를 실시간으로 본다
처리:
  1. 위젯별 데이터 조회
  2. 캐시 활용 (refresh_interval 기준)
  3. 데이터 반환
출력: 대시보드 데이터
위젯 예시:
  - 오늘 매출 (숫자, 전일 대비)
  - 시간대별 주문 (라인 차트)
  - 재고 부족 품목 (테이블)
  - 배송 진행 현황 (파이 차트)
```

#### 4-2. 정기 리포트 생성
```
시나리오: 정해진 시간에 리포트를 생성하여 발송한다
트리거: 스케줄러
처리:
  1. ReportSchedule 조회
  2. 쿼리 실행 (전일/전주/전월 기준)
  3. 포맷 변환 (PDF/Excel)
  4. 파일 저장 (ReportSnapshot)
  5. 수신자에게 이메일 발송
출력: 리포트 파일
```

#### 4-3. 맞춤 리포트 생성
```
시나리오: 사용자가 조건을 지정하여 리포트를 생성한다
입력: 리포트 유형, 기간, 필터 조건
처리:
  1. ReportDefinition 조회
  2. 파라미터 적용
  3. 쿼리 실행
  4. 결과 반환 (또는 파일 생성)
출력: 리포트 데이터
```

#### 4-4. 데이터 집계 (배치)
```
시나리오: 원본 데이터를 집계 테이블로 정리한다
트리거: 일간/월간 배치
처리:
  1. 원본 테이블에서 집계
  2. 집계 테이블에 저장 (DailySales, MonthlyStats...)
  3. 리포트는 집계 테이블 조회
목적: 리포트 조회 성능 향상
```

---

## Phase 5 완료 조건

### 필수 구현
- [ ] 고객 등급/상담 이력
- [ ] 포인트 적립/사용/소멸
- [ ] 쿠폰 발급/사용
- [ ] 알림 발송 (최소 1채널)
- [ ] 핵심 리포트 3종 이상

### 검증 시나리오
1. 주문 완료 → 포인트 적립 → 다음 주문에서 사용
2. 쿠폰 발급 → 조건 체크 → 사용 → 상태 변경
3. 배송 완료 → 알림 발송 → 수신 확인
4. 대시보드에서 오늘 매출 조회

### 문서화 필수
- [ ] 등급 산정 기준
- [ ] 포인트 정책
- [ ] 알림 트리거 목록
- [ ] 리포트 KPI 정의

---

## 전체 Phase 완료 후

모든 Phase가 완료되면:
- 하나의 ERP 시스템 완성
- 각 모듈이 유기적으로 연결
- "왜 이렇게 설계했는가"에 대한 근거 축적
- 면접에서 깊이 있는 대화 가능

```
[주문] → [재고예약] → [결제] → [출고지시] → [피킹] → [배송]
                                              ↓
                                         [배송완료]
                                              ↓
                                      [포인트 적립] → [알림]
                                              ↓
                                      [매출 집계] → [리포트]
```

이게 ERP의 전체 그림이야.