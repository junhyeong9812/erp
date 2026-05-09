import type { AgingSnapshot } from "@/lib/types/settlement";

export const AGING_SNAPSHOTS: readonly AgingSnapshot[] = [
  {
    id: 1,
    customerId: 4,
    invoiceTotal: 8_320_000,
    bucket0_30: 8_320_000,
    bucket31_60: 0,
    bucket61_90: 0,
    bucket90Plus: 0,
    asOf: "2026-05-04",
  },
  {
    id: 2,
    customerId: 6,
    invoiceTotal: 3_680_000,
    bucket0_30: 0,
    bucket31_60: 3_680_000,
    bucket61_90: 0,
    bucket90Plus: 0,
    asOf: "2026-05-04",
  },
  {
    id: 3,
    customerId: 3,
    invoiceTotal: 1_540_000,
    bucket0_30: 0,
    bucket31_60: 0,
    bucket61_90: 540_000,
    bucket90Plus: 1_000_000,
    asOf: "2026-05-04",
  },
];
