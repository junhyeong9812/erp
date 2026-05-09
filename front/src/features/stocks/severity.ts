export type StockSeverity = "critical" | "warning" | "normal";

export function severityOf(available: number): StockSeverity {
  if (available <= 5) return "critical";
  if (available <= 10) return "warning";
  return "normal";
}
