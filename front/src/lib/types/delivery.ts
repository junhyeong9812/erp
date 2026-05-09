export type DeliveryStatus =
  | "ASSIGNED"
  | "IN_TRANSIT"
  | "DELIVERED"
  | "RETURNED";

export interface Delivery {
  id: number;
  shipmentId: number;
  orderId: number;
  customerId: number;
  status: DeliveryStatus;
  eta: string;
  deliveredAt: string | null;
  courier: string;
  driver: string;
  region: string;
}
