export type StockMovementType =
  | "IN"
  | "OUT"
  | "RESERVE"
  | "RELEASE"
  | "ADJUST"
  | "TRANSFER";

export interface StockMovement {
  id: number;
  productId: number;
  warehouseId: number;
  type: StockMovementType;
  quantity: number;
  referenceId: number | null;
  reference: string | null;
  occurredAt: string;
  note: string | null;
}
