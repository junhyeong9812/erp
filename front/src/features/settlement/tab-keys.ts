export const SETTLEMENT_TABS = [
  "periods",
  "ledgers",
  "sellers",
  "aging",
  "batch",
] as const;
export type SettlementTabKey = (typeof SETTLEMENT_TABS)[number];

export function isSettlementTab(value: string): value is SettlementTabKey {
  return (SETTLEMENT_TABS as readonly string[]).includes(value);
}
