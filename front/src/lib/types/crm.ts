export type ConsultationChannel = "PHONE" | "EMAIL" | "CHAT" | "VISIT";
export type ConsultationStatus = "OPEN" | "RESOLVED";

export type ClaimType =
  | "SHIPPING"
  | "PRODUCT"
  | "REFUND"
  | "OTHER";
export type ClaimStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED";

export type PointStatus = "ACTIVE" | "USED" | "EXPIRED";
export type CouponStatus = "ISSUED" | "USED" | "EXPIRED";

export interface Consultation {
  id: number;
  customerId: number;
  channel: ConsultationChannel;
  summary: string;
  status: ConsultationStatus;
  occurredAt: string;
  agentName: string;
}

export interface Claim {
  id: number;
  customerId: number;
  type: ClaimType;
  description: string;
  status: ClaimStatus;
  filedAt: string;
  resolvedAt: string | null;
}

export interface CustomerPoint {
  id: number;
  customerId: number;
  amount: number;
  earnedAt: string;
  expireOn: string;
  status: PointStatus;
  source: string;
}

export interface Coupon {
  id: number;
  code: string;
  customerId: number | null;
  discountRate: number | null;
  discountAmount: number | null;
  expireOn: string;
  status: CouponStatus;
  issuedAt: string;
}
