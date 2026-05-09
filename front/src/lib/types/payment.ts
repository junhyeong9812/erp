export type PaymentMethod = "CARD" | "BANK" | "VIRTUAL_ACCOUNT";
export type PaymentStatus = "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED";

export interface Payment {
  id: number;
  orderId: number;
  method: PaymentMethod;
  amount: number;
  status: PaymentStatus;
  processedAt: string | null;
  pgReference: string | null;
}

export interface Refund {
  id: number;
  paymentId: number;
  amount: number;
  reason: string;
  processedAt: string;
}
