import type { SettlementPeriod } from "@/lib/types/settlement";

export const SETTLEMENT_PERIODS: readonly SettlementPeriod[] = [
  {
    id: 1,
    startDate: "2026-05-01",
    endDate: "2026-05-31",
    status: "OPEN",
    closedAt: null,
    settledAt: null,
  },
  {
    id: 2,
    startDate: "2026-04-01",
    endDate: "2026-04-30",
    status: "SETTLED",
    closedAt: "2026-05-01T00:10:00",
    settledAt: "2026-05-03T15:00:00",
  },
  {
    id: 3,
    startDate: "2026-03-01",
    endDate: "2026-03-31",
    status: "SETTLED",
    closedAt: "2026-04-01T00:08:00",
    settledAt: "2026-04-02T17:30:00",
  },
];
