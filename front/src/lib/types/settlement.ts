export type PeriodStatus = "OPEN" | "CLOSED" | "SETTLED";

export type LedgerType =
  | "SALES"
  | "REFUND"
  | "PURCHASE"
  | "FEE"
  | "ADJUSTMENT"
  | "REVERSAL";

export type SellerSettlementStatus = "CALCULATED" | "PAID";

export type AgingBucket = "BUCKET_0_30" | "BUCKET_31_60" | "BUCKET_61_90" | "BUCKET_90_PLUS";

export type BatchJobStatus = "COMPLETED" | "FAILED" | "RUNNING";

export interface SettlementPeriod {
  id: number;
  startDate: string;
  endDate: string;
  status: PeriodStatus;
  closedAt: string | null;
  settledAt: string | null;
}

export interface Ledger {
  id: number;
  periodId: number;
  type: LedgerType;
  referenceId: number;
  description: string;
  debit: number;
  credit: number;
  occurredAt: string;
  reversedBy: number | null;
}

export interface SellerSettlement {
  id: number;
  sellerId: number;
  periodId: number;
  grossSales: number;
  refundAmount: number;
  feeAmount: number;
  netPayout: number;
  status: SellerSettlementStatus;
  calculatedAt: string;
  paidAt: string | null;
}

export interface AgingSnapshot {
  id: number;
  customerId: number;
  invoiceTotal: number;
  bucket0_30: number;
  bucket31_60: number;
  bucket61_90: number;
  bucket90Plus: number;
  asOf: string;
}

export interface BatchJobLog {
  id: number;
  jobName: string;
  parameters: string;
  startedAt: string;
  endedAt: string | null;
  status: BatchJobStatus;
  readCount: number;
  writeCount: number;
  skipCount: number;
  failureMessage: string | null;
}
