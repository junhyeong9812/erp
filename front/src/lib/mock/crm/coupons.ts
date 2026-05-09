import type { Coupon } from "@/lib/types/crm";

export const COUPONS: readonly Coupon[] = [
  {
    id: 1,
    code: "WELCOME10",
    customerId: null,
    discountRate: 10,
    discountAmount: null,
    expireOn: "2026-12-31",
    status: "ISSUED",
    issuedAt: "2026-04-01T00:00:00",
  },
  {
    id: 2,
    code: "VIP-MAY-30K",
    customerId: 1,
    discountRate: null,
    discountAmount: 30000,
    expireOn: "2026-05-31",
    status: "ISSUED",
    issuedAt: "2026-05-01T00:00:00",
  },
  {
    id: 3,
    code: "GOLD-15PCT",
    customerId: 2,
    discountRate: 15,
    discountAmount: null,
    expireOn: "2026-06-30",
    status: "USED",
    issuedAt: "2026-04-10T00:00:00",
  },
  {
    id: 4,
    code: "WINTER-20K",
    customerId: 5,
    discountRate: null,
    discountAmount: 20000,
    expireOn: "2026-02-28",
    status: "EXPIRED",
    issuedAt: "2025-12-01T00:00:00",
  },
];
