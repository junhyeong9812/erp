export type ShipmentStatus = "PREPARING" | "DISPATCHED" | "COMPLETED";

export interface Shipment {
  id: number;
  orderId: number;
  customerId: number;
  status: ShipmentStatus;
  warehouseId: number;
  items: number;
  weightKg: number;
  driver: string | null;
  tracking: string | null;
  preparedAt: string | null;
  dispatchedAt: string | null;
  deliveredAt: string | null;
}
