import type { Quote } from "@/lib/types";

export const QUOTES: readonly Quote[] = [
  {
    id: 1024,
    customerId: 1,
    lines: [
      { productId: 100, quantity: 6, unitPrice: 1_580_000 },
      { productId: 102, quantity: 6, unitPrice: 184_000 },
    ],
    validUntil: "2026-05-31",
    status: "ACTIVE",
    total: 6 * 1_580_000 + 6 * 184_000,
    createdAt: "2026-05-03T14:32:00",
  },
  {
    id: 1025,
    customerId: 5,
    lines: [{ productId: 105, quantity: 8, unitPrice: 348_000 }],
    validUntil: "2026-05-15",
    status: "ACTIVE",
    total: 8 * 348_000,
    createdAt: "2026-05-04T09:30:00",
  },
  {
    id: 1023,
    customerId: 2,
    lines: [{ productId: 101, quantity: 12, unitPrice: 612_000 }],
    validUntil: "2026-05-04",
    status: "ACCEPTED",
    total: 12 * 612_000,
    createdAt: "2026-04-30T11:00:00",
  },
  {
    id: 1018,
    customerId: 3,
    lines: [{ productId: 103, quantity: 30, unitPrice: 58_000 }],
    validUntil: "2026-04-15",
    status: "EXPIRED",
    total: 30 * 58_000,
    createdAt: "2026-04-01T10:00:00",
  },
  {
    id: 1020,
    customerId: 6,
    lines: [{ productId: 107, quantity: 4, unitPrice: 198_000 }],
    validUntil: "2026-04-30",
    status: "REJECTED",
    total: 4 * 198_000,
    createdAt: "2026-04-15T14:00:00",
  },
];
