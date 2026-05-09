export type ReportType =
  | "DAILY_SALES"
  | "WEEKLY_SUMMARY"
  | "MONTHLY_PNL"
  | "SELLER_PAYOUT"
  | "AGING";

export interface ReportSnapshot {
  id: number;
  reportType: ReportType;
  targetDate: string;
  metrics: Record<string, number>;
  generatedAt: string;
}
