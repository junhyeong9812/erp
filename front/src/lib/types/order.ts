export type OrderStatus =
  | "PLACED"
  | "CONFIRMED"
  | "SHIPPED"
  | "COMPLETED"
  | "CANCELLED"
  | "REFUNDED";

export interface OrderLine {
  productId: number;
  quantity: number;
  unitPrice: number;
}

export interface Order {
  id: number;
  customerId: number;
  status: OrderStatus;
  placedAt: string;
  lines: OrderLine[];
  total: number;
  paid: boolean;
  shipmentId: number | null;
}
