import type { CustomerPoint } from "@/lib/types/crm";

export const POINTS: readonly CustomerPoint[] = [
  { id: 1, customerId: 1, amount: 80000, earnedAt: "2026-05-04T09:30:00", expireOn: "2027-05-04", status: "ACTIVE", source: "결제 적립 (1%)" },
  { id: 2, customerId: 1, amount: 12000, earnedAt: "2026-04-15T11:00:00", expireOn: "2027-04-15", status: "ACTIVE", source: "결제 적립 (1%)" },
  { id: 3, customerId: 2, amount: 73000, earnedAt: "2026-05-04T11:10:00", expireOn: "2027-05-04", status: "ACTIVE", source: "결제 적립 (1%)" },
  { id: 4, customerId: 5, amount: 24000, earnedAt: "2026-05-04T11:50:00", expireOn: "2027-05-04", status: "ACTIVE", source: "결제 적립 (1%)" },
  { id: 5, customerId: 7, amount: 29000, earnedAt: "2026-04-22T15:00:00", expireOn: "2027-04-22", status: "ACTIVE", source: "결제 적립 (1%)" },
  { id: 6, customerId: 3, amount: 5000,  earnedAt: "2025-05-01T00:00:00", expireOn: "2026-05-01", status: "EXPIRED", source: "결제 적립 (1%)" },
];
