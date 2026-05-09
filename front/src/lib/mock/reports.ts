import type { ReportSnapshot } from "@/lib/types";

export const REPORTS: readonly ReportSnapshot[] = [
  {
    id: 1,
    reportType: "DAILY_SALES",
    targetDate: "2026-05-04",
    metrics: { total_payment: 16_836_000, total_quantity: 38, total_orders: 8 },
    generatedAt: "2026-05-05T00:05:00",
  },
  {
    id: 2,
    reportType: "DAILY_SALES",
    targetDate: "2026-05-03",
    metrics: { total_payment: 12_640_000, total_quantity: 50, total_orders: 4 },
    generatedAt: "2026-05-04T00:05:00",
  },
  {
    id: 3,
    reportType: "WEEKLY_SUMMARY",
    targetDate: "2026-05-04",
    metrics: { total_payment: 92_400_000, refund_amount: 380_000, gross_profit: 18_400_000 },
    generatedAt: "2026-05-05T00:30:00",
  },
  {
    id: 4,
    reportType: "MONTHLY_PNL",
    targetDate: "2026-04-30",
    metrics: {
      revenue: 412_000_000,
      cogs: 312_400_000,
      operating_cost: 48_000_000,
      net_income: 51_600_000,
    },
    generatedAt: "2026-05-01T03:00:00",
  },
  {
    id: 5,
    reportType: "AGING",
    targetDate: "2026-05-04",
    metrics: { bucket_0_30: 8_320_000, bucket_31_60: 3_680_000, bucket_61_90: 540_000, bucket_90_plus: 1_000_000 },
    generatedAt: "2026-05-04T00:05:30",
  },
];
