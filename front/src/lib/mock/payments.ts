import type { Payment, Refund } from "@/lib/types";

export const PAYMENTS: readonly Payment[] = [
  { id: 1, orderId: 2406, method: "CARD",            amount: 7_056_000, status: "COMPLETED", processedAt: "2026-05-04T13:42:30", pgReference: "PG-CARD-A4F2" },
  { id: 2, orderId: 2407, method: "BANK",            amount: 7_344_000, status: "COMPLETED", processedAt: "2026-05-04T10:48:15", pgReference: "PG-BANK-B91E" },
  { id: 3, orderId: 2408, method: "CARD",            amount: 2_436_000, status: "COMPLETED", processedAt: "2026-05-04T11:25:50", pgReference: "PG-CARD-C7D1" },
  { id: 4, orderId: 2411, method: "VIRTUAL_ACCOUNT", amount: 3_160_000, status: "PENDING",   processedAt: null,                  pgReference: "PG-VA-D2C8" },
  { id: 5, orderId: 2400, method: "CARD",            amount: 1_580_000, status: "REFUNDED",  processedAt: "2026-04-22T10:30:00", pgReference: "PG-CARD-X1Y4" },
  { id: 6, orderId: 2399, method: "CARD",            amount: 612_000,   status: "FAILED",    processedAt: "2026-04-21T16:00:00", pgReference: "PG-CARD-Z9K2" },
];

export const REFUNDS: readonly Refund[] = [
  {
    id: 1,
    paymentId: 5,
    amount: 1_580_000,
    reason: "고객 변심 (전체 환불)",
    processedAt: "2026-04-25T11:00:00",
  },
  {
    id: 2,
    paymentId: 1,
    amount: 184_000,
    reason: "키보드 1개 부분 환불",
    processedAt: "2026-05-04T15:00:00",
  },
];
