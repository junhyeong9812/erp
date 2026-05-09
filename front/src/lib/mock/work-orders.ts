import type { WorkOrder } from "@/lib/types";

export const WORK_ORDERS: readonly WorkOrder[] = [
  { id: 1, productId: 100, plannedQuantity: 50, producedQuantity: 50, defectiveQuantity: 1, status: "COMPLETED",   issuedAt: "2026-04-15T09:00:00", completedAt: "2026-04-22T17:30:00" },
  { id: 2, productId: 102, plannedQuantity: 200, producedQuantity: 124, defectiveQuantity: 3, status: "IN_PROGRESS", issuedAt: "2026-04-28T09:00:00", completedAt: null },
  { id: 3, productId: 105, plannedQuantity: 30,  producedQuantity: 0,  defectiveQuantity: 0, status: "PLANNED",     issuedAt: "2026-05-04T10:00:00", completedAt: null },
  { id: 4, productId: 107, plannedQuantity: 20,  producedQuantity: 0,  defectiveQuantity: 0, status: "CANCELLED",   issuedAt: "2026-04-20T14:00:00", completedAt: null },
];
