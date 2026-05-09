export type PurchaseOrderStatus =
  | "ISSUED"
  | "PARTIAL"
  | "COMPLETED"
  | "CANCELLED";

export interface PurchaseOrder {
  id: number;
  supplier: string;
  productId: number;
  quantity: number;
  unitPrice: number;
  status: PurchaseOrderStatus;
  issuedAt: string;
  received?: number;
}
