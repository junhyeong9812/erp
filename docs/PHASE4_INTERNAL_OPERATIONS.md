# Phase 4: 내부 운영

> hr → approval → auth

## 목표

Phase 1-3은 "외부"와의 거래였다.
Phase 4는 "내부" 운영이다.

- 직원 관리, 급여 계산
- 문서 결재 프로세스
- 누가 무엇을 할 수 있는지 권한 관리

---

## 선행 조건

Phase 1-3 완료 권장:
- ✅ 핵심 비즈니스 흐름 (주문-결제-배송)
- ✅ 결재가 필요한 업무들 존재 (구매요청, 반품승인 등)

---

## 구현 순서

```
1. hr (인사/급여)      - 사람 관리
2. approval (전자결재) - 업무 승인 흐름
3. auth (권한관리)     - 접근 제어
```

---

## 1. HR (인사/급여)

### 핵심 엔티티

```
Department (부서)
├── id, name, parent_id, manager_id, level, status

Position (직급)
├── id, name, level, base_salary

Employee (직원)
├── id, employee_number, name, email, department_id, position_id
├── manager_id, hire_date, resignation_date, status

Attendance (근태)
├── id, employee_id, date, check_in, check_out
├── status (NORMAL, LATE, EARLY_LEAVE, ABSENT), work_hours, overtime_hours

LeaveBalance (휴가잔여)
├── id, employee_id, leave_type_id, year
├── total_days, used_days, remaining_days

LeaveRequest (휴가신청)
├── id, employee_id, leave_type_id, start_date, end_date
├── days, reason, status (PENDING, APPROVED, REJECTED)

Salary (급여)
├── id, employee_id, year_month
├── base_salary, overtime_pay, gross_salary
├── income_tax, national_pension, health_insurance
├── total_deduction, net_salary, status
```

### 핵심 시나리오

| 순서 | 시나리오 | 핵심 로직 |
|------|----------|-----------|
| 1 | 조직도 구성 | 부서 계층 구조 생성 |
| 2 | 직원 등록 (입사) | Employee 생성, 연차 부여, 계정 생성 |
| 3 | 인사이동/승진 | 부서/직급 변경, 이력 관리, 급여 조정 |
| 4 | 출퇴근 기록 | 지각/조퇴 판단, 근무시간 계산 |
| 5 | 휴가 신청 | 잔여 확인 → 결재 연동 |
| 6 | 휴가 승인 | 잔여일수 차감, 근태 반영 |
| 7 | 급여 계산 ⭐ | 기본급 + 수당 - 4대보험 - 세금 = 실수령 |
| 8 | 퇴사 처리 | 상태 변경, 계정 비활성화, 연차 정산 |

### 급여 계산 공식 (단순화)

```
총지급액 = 기본급 + 초과근무수당 + 기타수당
공제액 = 국민연금(4.5%) + 건강보험(3.5%) + 고용보험(0.9%) + 소득세
실수령액 = 총지급액 - 공제액
```

### 트레이드오프

| 상황 | 선택지 | 결정 기준 |
|------|--------|-----------|
| 급여 계산 | 단순화 vs 실제 세법 | 프로젝트 목적 |
| 근태 입력 | 실시간 vs 수동 | 인프라 |
| 연말정산 | 포함 vs 제외 | 복잡도 |

---

## 2. Approval (전자결재)

### 핵심 엔티티

```
ApprovalTemplate (결재양식)
├── id, name, document_type, default_approval_line_id

ApprovalLine (결재선)
├── id, name, template_id

ApprovalLineStep (결재 단계)
├── id, approval_line_id, step_order
├── type (APPROVAL, AGREEMENT, NOTIFY)
├── approver_type (FIXED_USER, DEPARTMENT_HEAD, REPORTER_MANAGER)

ApprovalDocument (결재문서)
├── id, document_number, template_id, title, content
├── reference_type, reference_id, requester_id
├── status (DRAFT, IN_PROGRESS, APPROVED, REJECTED), current_step

ApprovalHistory (결재이력)
├── id, document_id, step_order, approver_id
├── action (APPROVE, REJECT, DELEGATE), comment, acted_at

Delegation (대결/위임)
├── id, delegator_id, delegatee_id, start_date, end_date
```

### 핵심 시나리오

| 순서 | 시나리오 | 핵심 로직 |
|------|----------|-----------|
| 1 | 결재선 템플릿 등록 | 양식별 결재 단계 정의 |
| 2 | 결재 기안 ⭐ | 문서 생성, 결재자 확정, 알림 |
| 3 | 결재 승인/반려 ⭐ | 이력 기록, 다음 단계 or 완료/반려 |
| 4 | 대결/위임 설정 | 부재 시 대리 결재 |
| 5 | 결재 대기 목록 | 내가 처리할 문서 조회 |
| 6 | 후결 처리 | 긴급 건 선처리, 사후 결재 |

### 결재 상태 전이

```
DRAFT → IN_PROGRESS → APPROVED
                   ↘ REJECTED
                   
IN_PROGRESS에서:
- 승인 → 다음 단계 있으면 유지, 없으면 APPROVED
- 반려 → REJECTED
- 취소 → CANCELLED (기안자만)
```

---

## 3. Auth (권한/접근제어)

### 핵심 엔티티

```
User (사용자 계정)
├── id, employee_id, username, password_hash
├── status (ACTIVE, LOCKED, DISABLED), failed_attempts

Role (역할)
├── id, name, description
예: ADMIN, SALES_MANAGER, SALES_STAFF, HR_MANAGER, ACCOUNTANT

Permission (권한)
├── id, code, resource, action (READ, WRITE, DELETE, APPROVE)
예: INVENTORY_READ, SALARY_READ, ORDER_CREATE

RolePermission (역할-권한)
├── role_id, permission_id

UserRole (사용자-역할)
├── user_id, role_id

DataScope (데이터 범위)
├── id, role_id, resource
├── scope_type (ALL, DEPARTMENT, SELF)

AuditLog (감사로그)
├── id, user_id, action, resource, resource_id
├── old_value, new_value, ip_address, created_at
```

### 핵심 시나리오

| 순서 | 시나리오 | 핵심 로직 |
|------|----------|-----------|
| 1 | 역할/권한 정의 | RBAC 기본 구조 |
| 2 | 계정 생성 (입사 연동) | User 생성, 기본 역할 할당 |
| 3 | 역할 할당 | UserRole 매핑 |
| 4 | 권한 체크 ⭐ | API 호출 시 권한 검증 |
| 5 | 데이터 범위 필터링 ⭐ | 본인/부서 데이터만 조회 |
| 6 | 로그인/로그아웃 | 세션 관리, 실패 횟수 |
| 7 | 감사 로그 | 중요 작업 기록 |

### 권한 체크 흐름

```
API 요청 → 인터셉터
    ↓
사용자 역할 조회 (UserRole)
    ↓
역할의 권한 조회 (RolePermission)
    ↓
요청 권한 포함? → Yes: 통과
              → No: 403 Forbidden
```

### 데이터 범위 예시

```
영업사원 (SALES_STAFF):
  - Customer: SELF (본인 담당만)
  - Order: SELF (본인 주문만)
  - Product: ALL (전체 조회)

영업팀장 (SALES_MANAGER):
  - Customer: DEPARTMENT (팀 전체)
  - Order: DEPARTMENT
  - Salary: NONE (접근 불가)
```

---

## Phase 4 완료 조건

### 필수 구현
- [ ] 직원/부서 관리
- [ ] 근태/휴가 기본 기능
- [ ] 급여 계산 (단순화)
- [ ] 결재 기안/승인/반려
- [ ] RBAC 권한 체크
- [ ] 로그인/감사로그

### 검증 시나리오
1. 입사 → 계정 생성 → 역할 할당 → 로그인
2. 휴가 신청 → 결재 → 승인 → 잔여일수 차감
3. 권한 없는 메뉴 접근 → 거부 + 로그
4. 영업사원 로그인 → 본인 고객만 조회

### 문서화 필수
- [ ] 역할/권한 매트릭스
- [ ] 결재 상태 전이도
- [ ] 급여 계산 로직

---

## 다음 Phase와의 연결

- 모든 모듈에 권한 체크 적용
- 승인 필요 업무에 전자결재 연동
- **CRM**: 고객 담당자 연결
- **Report**: 급여/근태 통계