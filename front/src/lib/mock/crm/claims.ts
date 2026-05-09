import type { Claim } from "@/lib/types/crm";

export const CLAIMS: readonly Claim[] = [
  {
    id: 1,
    customerId: 3,
    type: "SHIPPING",
    description: "배송 박스 파손, 제품 일부 흠집",
    status: "IN_PROGRESS",
    filedAt: "2026-05-03T11:20:00",
    resolvedAt: null,
  },
  {
    id: 2,
    customerId: 1,
    type: "PRODUCT",
    description: "노트북 키보드 일부 키 미작동",
    status: "OPEN",
    filedAt: "2026-05-04T08:50:00",
    resolvedAt: null,
  },
  {
    id: 3,
    customerId: 7,
    type: "REFUND",
    description: "주문 취소 후 환불 처리 지연",
    status: "RESOLVED",
    filedAt: "2026-04-28T16:00:00",
    resolvedAt: "2026-05-01T10:30:00",
  },
];
