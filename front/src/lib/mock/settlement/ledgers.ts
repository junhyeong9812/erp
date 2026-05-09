import type { Ledger } from "@/lib/types/settlement";

export const LEDGERS: readonly Ledger[] = [
  { id: 1,  periodId: 1, type: "SALES",      referenceId: 2406, description: "주문 #2406 매출",   debit: 0,         credit: 7_056_000, occurredAt: "2026-05-04T13:42:00", reversedBy: null },
  { id: 2,  periodId: 1, type: "SALES",      referenceId: 2407, description: "주문 #2407 매출",   debit: 0,         credit: 7_344_000, occurredAt: "2026-05-04T10:48:00", reversedBy: null },
  { id: 3,  periodId: 1, type: "SALES",      referenceId: 2408, description: "주문 #2408 매출",   debit: 0,         credit: 2_436_000, occurredAt: "2026-05-04T11:25:00", reversedBy: null },
  { id: 4,  periodId: 1, type: "FEE",        referenceId: 2406, description: "결제 수수료 (PG)",   debit: 70_560,    credit: 0,         occurredAt: "2026-05-04T13:43:00", reversedBy: null },
  { id: 5,  periodId: 1, type: "PURCHASE",   referenceId: 5008, description: "발주 #5008 매입",   debit: 59_000_000, credit: 0,        occurredAt: "2026-04-28T09:00:00", reversedBy: null },
  { id: 6,  periodId: 2, type: "ADJUSTMENT", referenceId: 0,    description: "전월 누락 매출 조정", debit: 0,         credit: 250_000,    occurredAt: "2026-04-30T18:00:00", reversedBy: null },
  { id: 7,  periodId: 2, type: "REFUND",     referenceId: 2400, description: "주문 #2400 환불",   debit: 380_000,    credit: 0,         occurredAt: "2026-04-22T11:30:00", reversedBy: null },
  { id: 8,  periodId: 2, type: "REVERSAL",   referenceId: 6,    description: "전표 #6 반대전표",   debit: 250_000,    credit: 0,         occurredAt: "2026-04-30T19:00:00", reversedBy: null },
];
