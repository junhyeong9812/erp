import type { OrderStatus } from "./order";
import type { ShipmentStatus } from "./shipment";
import type { DeliveryStatus } from "./delivery";
import type { PurchaseOrderStatus } from "./purchase-order";

export type BadgeTone =
  | "ok"
  | "warn"
  | "info"
  | "danger"
  | "neutral"
  | "accent";

export type StatusKey =
  | OrderStatus
  | ShipmentStatus
  | DeliveryStatus
  | PurchaseOrderStatus
  | "PENDING";

export const STATUS_TONE: Record<StatusKey, BadgeTone> = {
  PLACED: "info",
  CONFIRMED: "accent",
  SHIPPED: "warn",
  COMPLETED: "ok",
  CANCELLED: "danger",
  REFUNDED: "danger",
  PREPARING: "info",
  DISPATCHED: "warn",
  ASSIGNED: "info",
  IN_TRANSIT: "warn",
  DELIVERED: "ok",
  RETURNED: "danger",
  ISSUED: "info",
  PARTIAL: "warn",
  PENDING: "warn",
};

export const ORDER_STEPS: OrderStatus[] = [
  "PLACED",
  "CONFIRMED",
  "SHIPPED",
  "COMPLETED",
];

export const SHIPMENT_STEPS: ShipmentStatus[] = [
  "PREPARING",
  "DISPATCHED",
  "COMPLETED",
];

export const DELIVERY_STEPS: DeliveryStatus[] = [
  "ASSIGNED",
  "IN_TRANSIT",
  "DELIVERED",
];
