import type { SettlementPeriod } from "@/lib/types/settlement";
import { SETTLEMENT_PERIODS } from "./periods";

export { SETTLEMENT_PERIODS } from "./periods";
export { LEDGERS } from "./ledgers";
export { SELLER_SETTLEMENTS } from "./seller-settlements";
export { AGING_SNAPSHOTS } from "./aging";
export { BATCH_LOGS } from "./batch-logs";

const PLACEHOLDER_PERIOD: SettlementPeriod = {
  id: 0,
  startDate: "—",
  endDate: "—",
  status: "OPEN",
  closedAt: null,
  settledAt: null,
};

export function lookupPeriod(id: number): SettlementPeriod {
  return SETTLEMENT_PERIODS.find((p) => p.id === id) ?? PLACEHOLDER_PERIOD;
}
