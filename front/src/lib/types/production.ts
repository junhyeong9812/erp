export type WorkOrderStatus =
  | "PLANNED"
  | "IN_PROGRESS"
  | "COMPLETED"
  | "CANCELLED";

export interface WorkOrder {
  id: number;
  productId: number;
  plannedQuantity: number;
  producedQuantity: number;
  defectiveQuantity: number;
  status: WorkOrderStatus;
  issuedAt: string;
  completedAt: string | null;
}
