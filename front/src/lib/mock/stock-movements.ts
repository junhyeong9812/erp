import type { StockMovement } from "@/lib/types";

export const STOCK_MOVEMENTS: readonly StockMovement[] = [
  { id: 1,  productId: 100, warehouseId: 1, type: "IN",       quantity: 50,  referenceId: 5008, reference: "PO-5008",  occurredAt: "2026-04-28T09:30:00", note: "ABC상사 발주 입고" },
  { id: 2,  productId: 102, warehouseId: 1, type: "IN",       quantity: 200, referenceId: 5007, reference: "PO-5007",  occurredAt: "2026-04-22T14:30:00", note: "한미테크 발주 입고" },
  { id: 3,  productId: 100, warehouseId: 1, type: "RESERVE",  quantity: 4,   referenceId: 2406, reference: "ORD-2406", occurredAt: "2026-05-04T09:15:00", note: "ACME Corp 주문 예약" },
  { id: 4,  productId: 101, warehouseId: 2, type: "RESERVE",  quantity: 12,  referenceId: 2407, reference: "ORD-2407", occurredAt: "2026-05-04T10:50:00", note: "한솔로지스 주문 예약" },
  { id: 5,  productId: 100, warehouseId: 1, type: "OUT",      quantity: 4,   referenceId: 7012, reference: "SHP-7012", occurredAt: "2026-05-04T10:08:00", note: "출고 발송" },
  { id: 6,  productId: 102, warehouseId: 1, type: "TRANSFER", quantity: 30,  referenceId: null, reference: null,        occurredAt: "2026-05-03T15:00:00", note: "본사창고 → 경기 물류센터" },
  { id: 7,  productId: 105, warehouseId: 1, type: "ADJUST",   quantity: -2,  referenceId: null, reference: null,        occurredAt: "2026-05-02T11:00:00", note: "월말 실사 차이 (분실)" },
  { id: 8,  productId: 107, warehouseId: 3, type: "ADJUST",   quantity: -4,  referenceId: null, reference: null,        occurredAt: "2026-05-04T08:00:00", note: "파손 폐기" },
  { id: 9,  productId: 103, warehouseId: 1, type: "RELEASE",  quantity: 2,   referenceId: 2410, reference: "ORD-2410", occurredAt: "2026-05-04T13:20:00", note: "주문 취소 — 예약 해제" },
];
