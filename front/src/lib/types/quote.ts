export type QuoteStatus = "ACTIVE" | "ACCEPTED" | "EXPIRED" | "REJECTED";

export interface QuoteLine {
  productId: number;
  quantity: number;
  unitPrice: number;
}

export interface Quote {
  id: number;
  customerId: number;
  lines: QuoteLine[];
  validUntil: string;
  status: QuoteStatus;
  total: number;
  createdAt: string;
}
