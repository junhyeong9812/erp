/* global React, CUSTOMERS, PRODUCTS, WAREHOUSES, lookupProduct, lookupCustomer */
// Extended seed data for the remaining domains.

const SUPPLIERS = [
  { id: 1, code: "SUP-001", name: "ABC상사",     contact: "010-1234-5678", category: "전자/컴포넌트", grade: "A" },
  { id: 2, code: "SUP-002", name: "동성유통",     contact: "010-2345-6789", category: "주변기기",     grade: "A" },
  { id: 3, code: "SUP-003", name: "글로벌소싱",   contact: "010-3456-7890", category: "도킹/케이블",   grade: "B" },
  { id: 4, code: "SUP-004", name: "한미테크",     contact: "010-4567-8901", category: "키보드/마우스", grade: "A" },
  { id: 5, code: "SUP-005", name: "Pacific Co.",  contact: "010-5678-9012", category: "오디오",       grade: "B" },
];

const QUOTES = [
  { id: 401, customerId: 1, status: "ACCEPTED", validUntil: "2026-05-31", total: 7_640_000, lines: [{ productId: 100, quantity: 4, unitPrice: 1_580_000 }, { productId: 102, quantity: 4, unitPrice: 184_000 }], createdAt: "2026-05-02T10:30:00" },
  { id: 402, customerId: 5, status: "ACTIVE",   validUntil: "2026-05-25", total: 2_436_000, lines: [{ productId: 105, quantity: 6, unitPrice: 348_000 }, { productId: 103, quantity: 6, unitPrice: 58_000 }], createdAt: "2026-05-03T14:12:00" },
  { id: 403, customerId: 4, status: "ACTIVE",   validUntil: "2026-05-20", total: 2_720_000, lines: [{ productId: 107, quantity: 8, unitPrice: 198_000 }, { productId: 104, quantity: 8, unitPrice: 142_000 }], createdAt: "2026-05-03T09:48:00" },
  { id: 404, customerId: 3, status: "EXPIRED",  validUntil: "2026-05-01", total: 678_000,   lines: [{ productId: 106, quantity: 3, unitPrice: 226_000 }], createdAt: "2026-04-25T16:20:00" },
  { id: 405, customerId: 2, status: "REJECTED", validUntil: "2026-04-30", total: 920_000,   lines: [{ productId: 102, quantity: 5, unitPrice: 184_000 }], createdAt: "2026-04-22T11:00:00" },
];

const PAYMENTS = [
  { id: 8001, orderId: 2406, amount: 7_056_000, method: "CARD",            status: "COMPLETED", createdAt: "2026-05-04T09:14:00" },
  { id: 8002, orderId: 2407, amount: 7_344_000, method: "BANK",            status: "COMPLETED", createdAt: "2026-05-04T10:50:00" },
  { id: 8003, orderId: 2408, amount: 2_436_000, method: "CARD",            status: "COMPLETED", createdAt: "2026-05-04T11:28:00" },
  { id: 8004, orderId: 2411, amount: 3_160_000, method: "VIRTUAL_ACCOUNT", status: "COMPLETED", createdAt: "2026-05-04T14:13:00" },
  { id: 8005, orderId: 2413, amount: 2_900_000, method: "CARD",            status: "REFUNDED",  createdAt: "2026-05-03T16:25:00", refundedAmount: 580_000 },
  { id: 8006, orderId: 2409, amount: 2_720_000, method: "CARD",            status: "PENDING",   createdAt: "2026-05-04T13:05:00" },
];

const REFUNDS = [
  { id: 9101, paymentId: 8005, orderId: 2413, amount: 580_000, reason: "품질 이슈 — 부분 환불", status: "COMPLETED", createdAt: "2026-05-04T11:40:00" },
];

const SETTLEMENT_PERIODS = [
  { id: 31, startDate: "2026-04-01", endDate: "2026-04-30", status: "CLOSED",   totalSales: 142_580_000, totalRefund: 3_240_000 },
  { id: 32, startDate: "2026-05-01", endDate: "2026-05-31", status: "OPEN",     totalSales:  21_896_000, totalRefund:   580_000 },
  { id: 30, startDate: "2026-03-01", endDate: "2026-03-31", status: "SETTLED",  totalSales: 128_440_000, totalRefund: 2_180_000 },
];

const LEDGERS = [
  { id: 5001, periodId: 32, type: "SALES",     referenceId: 2406, amount: 7_056_000, description: "Sales order #2406",  createdAt: "2026-05-04T09:14:00" },
  { id: 5002, periodId: 32, type: "SALES",     referenceId: 2407, amount: 7_344_000, description: "Sales order #2407",  createdAt: "2026-05-04T10:50:00" },
  { id: 5003, periodId: 32, type: "SALES",     referenceId: 2408, amount: 2_436_000, description: "Sales order #2408",  createdAt: "2026-05-04T11:28:00" },
  { id: 5004, periodId: 32, type: "SALES",     referenceId: 2411, amount: 3_160_000, description: "Sales order #2411",  createdAt: "2026-05-04T14:13:00" },
  { id: 5005, periodId: 32, type: "REFUND",    referenceId: 9101, amount: -580_000,  description: "Refund #9101",       createdAt: "2026-05-04T11:40:00" },
  { id: 5006, periodId: 32, type: "PURCHASE",  referenceId: 5009, amount: -9_920_000, description: "PO-5009 receive",    createdAt: "2026-05-01T11:30:00" },
  { id: 5007, periodId: 32, type: "FEE",       referenceId: 1,    amount: -284_000,  description: "PG fee",              createdAt: "2026-05-04T20:00:00" },
];

const CRM_CUSTOMERS = CUSTOMERS.map((c, i) => ({
  ...c,
  contact: `010-${1000 + i}-${4000 + i * 11}`,
  totalSales: [12_400_000, 8_240_000, 3_180_000, 6_820_000, 14_200_000, 920_000, 2_640_000, 5_820_000][i],
  openClaims: [0, 1, 0, 0, 2, 0, 0, 1][i],
  assigned: ["서준", "민지", "현우", "지윤", "서준", "민지", "현우", "지윤"][i],
  joinedAt: "2024-08-15",
}));

const CONSULTATIONS = [
  { id: 701, customerId: 2, type: "문의", title: "월별 정기배송 단가 협의",      handler: "민지", createdAt: "2026-05-04T11:20:00" },
  { id: 702, customerId: 5, type: "클레임", title: "헤드폰 일부 노이즈 발생",     handler: "서준", createdAt: "2026-05-04T09:48:00" },
  { id: 703, customerId: 1, type: "문의", title: "신제품 노트북 견적 요청",      handler: "서준", createdAt: "2026-05-03T16:00:00" },
];

const CLAIMS = [
  { id: 601, customerId: 2, status: "OPEN",        title: "운송 중 외관 손상", openedAt: "2026-05-03T14:00:00", priority: "HIGH" },
  { id: 602, customerId: 5, status: "IN_PROGRESS", title: "헤드폰 노이즈",     openedAt: "2026-05-04T09:48:00", priority: "MEDIUM" },
  { id: 603, customerId: 5, status: "OPEN",        title: "수량 불일치",       openedAt: "2026-05-04T13:10:00", priority: "MEDIUM" },
  { id: 604, customerId: 8, status: "RESOLVED",    title: "송장번호 오류",     openedAt: "2026-05-02T10:00:00", priority: "LOW" },
];

// Supplier-side claims (defective receipts).  status flow:
//   DRAFT → SENT → SUPPLIER_REVIEW → RESOLVED (REFUND | REPLACEMENT) | REJECTED
const SUPPLIER_CLAIMS = [
  { id: 5101, poId: 5009, supplier: "동성유통",   supplierId: 2, productId: 105, defectQty: 3, totalReceived: 22,
    defectType: "DAMAGED",       status: "SENT",            openedAt: "2026-05-02T15:30:00", priority: "MEDIUM",
    description: "외관 스크래치 다수, 박스 파손",
    history: [
      { ts: "2026-05-02T15:00:00", actor: "system", event: "DRAFT_CREATED", note: "입고 검수에서 자동 초안 작성 (3개 불량)" },
      { ts: "2026-05-02T15:30:00", actor: "현우",   event: "SENT_TO_SUPPLIER", note: "공급자에게 클레임 송부" },
    ]},
  { id: 5102, poId: 5008, supplier: "ABC상사",    supplierId: 1, productId: 100, defectQty: 1, totalReceived: 50,
    defectType: "QUANTITY_SHORT", status: "SUPPLIER_REVIEW", openedAt: "2026-04-30T11:00:00", priority: "HIGH",
    description: "1대 미입고 (포장 봉인 정상)",
    supplierResponse: { ts: "2026-05-01T10:30:00", note: "출하 기록 확인 중", proposed: null },
    history: [
      { ts: "2026-04-30T10:30:00", actor: "system", event: "DRAFT_CREATED", note: "입고 검수에서 자동 초안 작성" },
      { ts: "2026-04-30T11:00:00", actor: "현우",   event: "SENT_TO_SUPPLIER", note: "공급자에게 클레임 송부" },
      { ts: "2026-05-01T10:30:00", actor: "ABC상사 (공급자)", event: "SUPPLIER_REVIEWING", note: "출하 기록 확인 중 (회신 대기)" },
    ]},
  { id: 5103, poId: 5007, supplier: "한미테크",   supplierId: 4, productId: 102, defectQty: 6, totalReceived: 200,
    defectType: "DEFECTIVE",     status: "RESOLVED", resolutionType: "REPLACEMENT",
    openedAt: "2026-04-23T09:00:00", resolvedAt: "2026-04-25T14:30:00", priority: "MEDIUM",
    description: "키 입력 인식 불량 (6개)",
    supplierResponse: { ts: "2026-04-24T11:00:00", note: "전수 검사 결과 동일 lot 일부 결함 확인", proposed: "REPLACEMENT" },
    history: [
      { ts: "2026-04-23T09:00:00", actor: "system",          event: "DRAFT_CREATED",      note: "입고 검수에서 자동 초안 작성" },
      { ts: "2026-04-23T09:30:00", actor: "현우",            event: "SENT_TO_SUPPLIER",   note: "공급자에게 클레임 송부" },
      { ts: "2026-04-24T11:00:00", actor: "한미테크 (공급자)", event: "RESPONSE_RECEIVED",  note: "교환 제안 — 동일 모델 6개" },
      { ts: "2026-04-25T14:30:00", actor: "현우",            event: "RESOLVED",           note: "교환품 입고 완료, 클레임 종결" },
    ]},
  { id: 5104, poId: 5010, supplier: "글로벌소싱", supplierId: 3, productId: 107, defectQty: 4, totalReceived: 60,
    defectType: "WRONG_ITEM",    status: "RESOLVED", resolutionType: "REFUND",
    openedAt: "2026-04-20T13:00:00", resolvedAt: "2026-04-22T16:00:00", priority: "HIGH",
    description: "다른 모델 4개 혼입",
    supplierResponse: { ts: "2026-04-21T09:30:00", note: "오출고 인정", proposed: "REFUND" },
    history: [
      { ts: "2026-04-20T13:00:00", actor: "system",            event: "DRAFT_CREATED",      note: "입고 검수에서 자동 초안 작성" },
      { ts: "2026-04-20T14:00:00", actor: "현우",              event: "SENT_TO_SUPPLIER",   note: "공급자에게 클레임 송부" },
      { ts: "2026-04-21T09:30:00", actor: "글로벌소싱 (공급자)", event: "RESPONSE_RECEIVED",  note: "오출고 인정, 환불 제안" },
      { ts: "2026-04-22T16:00:00", actor: "지윤",              event: "RESOLVED",           note: "환불 처리 완료, 정산 전표 발행" },
    ]},
];

const NOTIFICATIONS = [
  { id: 9001, recipientId: 1, channel: "EMAIL", title: "결제 완료",      body: "주문 #2406 결제가 완료되었습니다.",       status: "SENT",    createdAt: "2026-05-04T09:14:30" },
  { id: 9002, recipientId: 2, channel: "EMAIL", title: "출고 시작",      body: "주문 #2407이 출고 처리되었습니다.",       status: "SENT",    createdAt: "2026-05-04T12:18:30" },
  { id: 9003, recipientId: 1, channel: "SYSTEM", title: "결재 요청",     body: "출장비 결재가 도착했습니다.",             status: "SENT",    createdAt: "2026-05-04T10:00:00" },
  { id: 9004, recipientId: 5, channel: "SMS",   title: "배송 출발",      body: "주문하신 상품이 출발했습니다. ETA 17:00",  status: "SENT",    createdAt: "2026-05-04T13:24:30" },
  { id: 9005, recipientId: 3, channel: "PUSH",  title: "재고 경고",      body: "USB-C 도킹 스테이션 재고 부족",            status: "FAILED",  createdAt: "2026-05-04T08:00:00" },
  { id: 9006, recipientId: 7, channel: "EMAIL", title: "배송 완료",      body: "주문 #2413이 배송 완료되었습니다.",         status: "SENT",    createdAt: "2026-05-04T09:55:30" },
];

const REPORTS = [
  { id: 1101, reportType: "DAILY_SALES",  targetDate: "2026-05-04", metrics: { total_payment: 19_996_000, total_quantity: 53, orders: 8 }, createdAt: "2026-05-04T00:05:00" },
  { id: 1102, reportType: "DAILY_SALES",  targetDate: "2026-05-03", metrics: { total_payment: 14_320_000, total_quantity: 41, orders: 6 }, createdAt: "2026-05-03T00:05:00" },
  { id: 1103, reportType: "WEEKLY_SALES", targetDate: "2026-04-27", metrics: { total_payment: 88_400_000, total_quantity: 240, orders: 39 }, createdAt: "2026-05-03T01:00:00" },
  { id: 1104, reportType: "INVENTORY",    targetDate: "2026-05-04", metrics: { skus_low: 2, skus_critical: 1, total_value: 412_800_000 }, createdAt: "2026-05-04T00:10:00" },
];

const APPROVALS = [
  { id: 301, drafterId: 1, drafterName: "서준", documentType: "EXPENSE",  title: "5월 출장비 결재",      status: "IN_PROGRESS", currentStep: 2, totalSteps: 3, createdAt: "2026-05-04T10:00:00", amount: 480_000, summary: "부산 거래처 미팅 출장비 (KTX·숙박·식대)", steps: [
    { order: 1, role: "팀장",  approver: "서준", status: "APPROVED",   actedAt: "2026-05-04T10:18:00", comment: "확인 완료" },
    { order: 2, role: "본부장", approver: "지윤", status: "PENDING",    actedAt: null, comment: null },
    { order: 3, role: "재무",  approver: "지윤", status: "WAITING",    actedAt: null, comment: null },
  ]},
  { id: 302, drafterId: 2, drafterName: "민지", documentType: "PURCHASE", title: "신규 모니터 30대 구매", status: "IN_PROGRESS", currentStep: 1, totalSteps: 3, createdAt: "2026-05-04T13:30:00", amount: 12_600_000, summary: "영업팀 모니터 교체 30대", steps: [
    { order: 1, role: "팀장",  approver: "민지", status: "PENDING",    actedAt: null, comment: null },
    { order: 2, role: "구매",  approver: "현우", status: "WAITING",    actedAt: null, comment: null },
    { order: 3, role: "재무",  approver: "지윤", status: "WAITING",    actedAt: null, comment: null },
  ]},
  { id: 303, drafterId: 3, drafterName: "현우", documentType: "LEAVE",    title: "5/12-14 연차 신청",     status: "APPROVED",    currentStep: 2, totalSteps: 2, createdAt: "2026-05-03T09:00:00", amount: 0, summary: "연차 3일 (5/12 월 ~ 5/14 수)", steps: [
    { order: 1, role: "팀장",  approver: "현우", status: "APPROVED",   actedAt: "2026-05-03T09:42:00", comment: "휴가 잘 다녀와요" },
    { order: 2, role: "인사",  approver: "은채", status: "APPROVED",   actedAt: "2026-05-03T11:10:00", comment: "처리 완료" },
  ]},
  { id: 304, drafterId: 4, drafterName: "지윤", documentType: "EXPENSE",  title: "거래처 접대비",         status: "REJECTED",    currentStep: 1, totalSteps: 2, createdAt: "2026-05-02T14:00:00", amount: 320_000, summary: "ABC상사 분기 미팅 식사", steps: [
    { order: 1, role: "팀장",  approver: "지윤", status: "REJECTED",   actedAt: "2026-05-02T16:30:00", comment: "한도 초과 — 재기안 요청" },
    { order: 2, role: "재무",  approver: "지윤", status: "WAITING",    actedAt: null, comment: null },
  ]},
  { id: 305, drafterId: 1, drafterName: "서준", documentType: "PURCHASE", title: "노트북 5대 추가",        status: "APPROVED",    currentStep: 3, totalSteps: 3, createdAt: "2026-05-01T11:00:00", amount: 8_900_000, summary: "신규 입사자 5인 노트북", steps: [
    { order: 1, role: "팀장",  approver: "서준", status: "APPROVED",   actedAt: "2026-05-01T11:24:00", comment: "승인" },
    { order: 2, role: "구매",  approver: "현우", status: "APPROVED",   actedAt: "2026-05-01T13:50:00", comment: "발주 진행" },
    { order: 3, role: "재무",  approver: "지윤", status: "APPROVED",   actedAt: "2026-05-01T16:08:00", comment: "예산 확인" },
  ]},
];

const EMPLOYEES = [
  { id: 1, employeeNumber: "E2024001", name: "서준",   department: "물류팀",   position: "매니저", joinedAt: "2024-03-01", baseSalary: 4_200_000, status: "ACTIVE" },
  { id: 2, employeeNumber: "E2024002", name: "민지",   department: "영업팀",   position: "대리",   joinedAt: "2024-04-15", baseSalary: 3_600_000, status: "ACTIVE" },
  { id: 3, employeeNumber: "E2024003", name: "현우",   department: "구매팀",   position: "사원",   joinedAt: "2024-09-01", baseSalary: 3_100_000, status: "ACTIVE" },
  { id: 4, employeeNumber: "E2024004", name: "지윤",   department: "재무팀",   position: "과장",   joinedAt: "2023-08-10", baseSalary: 4_800_000, status: "ACTIVE" },
  { id: 5, employeeNumber: "E2024005", name: "도연",   department: "물류팀",   position: "사원",   joinedAt: "2025-02-01", baseSalary: 3_000_000, status: "ACTIVE" },
  { id: 6, employeeNumber: "E2024006", name: "은채",   department: "CS팀",     position: "대리",   joinedAt: "2024-06-20", baseSalary: 3_500_000, status: "ACTIVE" },
];

const PAYROLLS = [
  { id: 7001, employeeId: 1, year: 2026, month: 4, baseSalary: 4_200_000, allowance: 500_000, insurance: 470_000, netSalary: 4_230_000 },
  { id: 7002, employeeId: 2, year: 2026, month: 4, baseSalary: 3_600_000, allowance: 300_000, insurance: 390_000, netSalary: 3_510_000 },
  { id: 7003, employeeId: 3, year: 2026, month: 4, baseSalary: 3_100_000, allowance: 200_000, insurance: 330_000, netSalary: 2_970_000 },
  { id: 7004, employeeId: 4, year: 2026, month: 4, baseSalary: 4_800_000, allowance: 600_000, insurance: 540_000, netSalary: 4_860_000 },
  { id: 7005, employeeId: 5, year: 2026, month: 4, baseSalary: 3_000_000, allowance: 200_000, insurance: 320_000, netSalary: 2_880_000 },
  { id: 7006, employeeId: 6, year: 2026, month: 4, baseSalary: 3_500_000, allowance: 300_000, insurance: 380_000, netSalary: 3_420_000 },
];

const WORK_ORDERS = [
  { id: 6001, productId: 100, plannedQuantity: 50, produced: 50, defective: 1, status: "COMPLETED",   issuedAt: "2026-04-28" },
  { id: 6002, productId: 101, plannedQuantity: 30, produced: 18, defective: 0, status: "IN_PROGRESS", issuedAt: "2026-05-02" },
  { id: 6003, productId: 105, plannedQuantity: 40, produced: 0,  defective: 0, status: "PLANNED",     issuedAt: "2026-05-04" },
];

Object.assign(window, {
  SUPPLIERS, QUOTES, PAYMENTS, REFUNDS, SETTLEMENT_PERIODS, LEDGERS,
  CRM_CUSTOMERS, CONSULTATIONS, CLAIMS, SUPPLIER_CLAIMS, NOTIFICATIONS, REPORTS, APPROVALS,
  EMPLOYEES, PAYROLLS, WORK_ORDERS,
});
