export type SellerTier = "STANDARD" | "PREMIUM" | "ENTERPRISE";

export interface Seller {
  id: number;
  code: string;
  name: string;
  tier: SellerTier;
  contact: string;
}
