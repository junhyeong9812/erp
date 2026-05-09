import { PRODUCTS } from "@/lib/mock";
import type {
  Order,
  OrderLine,
  Product,
  Shipment,
  Stock,
} from "@/lib/types";

export function sellerProducts(sellerId: number): Product[] {
  return PRODUCTS.filter((p) => p.sellerId === sellerId);
}

export function sellerProductIdSet(sellerId: number): Set<number> {
  return new Set(sellerProducts(sellerId).map((p) => p.id));
}

export function sellerStocks(sellerId: number, all: Stock[]): Stock[] {
  const ids = sellerProductIdSet(sellerId);
  return all.filter((s) => ids.has(s.productId));
}

export function ownLines(order: Order, ids: Set<number>): OrderLine[] {
  return order.lines.filter((l) => ids.has(l.productId));
}

export function isSellerOrder(order: Order, ids: Set<number>): boolean {
  return ownLines(order, ids).length > 0;
}

export function sellerOrders(sellerId: number, orders: Order[]): Order[] {
  const ids = sellerProductIdSet(sellerId);
  return orders.filter((o) => isSellerOrder(o, ids));
}

export function sellerOrderAmount(order: Order, ids: Set<number>): number {
  return ownLines(order, ids).reduce(
    (sum, l) => sum + l.quantity * l.unitPrice,
    0
  );
}

export function sellerOrderItems(order: Order, ids: Set<number>): number {
  return ownLines(order, ids).reduce((sum, l) => sum + l.quantity, 0);
}

export function sellerShipments(
  sellerId: number,
  shipments: Shipment[],
  orders: Order[]
): Shipment[] {
  const ids = sellerProductIdSet(sellerId);
  const orderById = new Map(orders.map((o) => [o.id, o]));
  return shipments.filter((s) => {
    const o = orderById.get(s.orderId);
    if (!o) return false;
    return isSellerOrder(o, ids);
  });
}

export function shipmentSellerItems(
  shipment: Shipment,
  sellerId: number,
  orders: Order[]
): number {
  const ids = sellerProductIdSet(sellerId);
  const order = orders.find((o) => o.id === shipment.orderId);
  if (!order) return 0;
  return sellerOrderItems(order, ids);
}

const PLATFORM_FEE_RATE = 0.05;

export interface SellerSettlementSnapshot {
  grossRevenue: number;
  fee: number;
  netPayout: number;
  byProduct: Array<{ productId: number; quantity: number; revenue: number }>;
}

export function sellerSettlementSnapshot(
  sellerId: number,
  orders: Order[]
): SellerSettlementSnapshot {
  const ids = sellerProductIdSet(sellerId);
  const completed = orders.filter((o) => o.status === "COMPLETED");

  const productMap = new Map<number, { quantity: number; revenue: number }>();
  let grossRevenue = 0;

  for (const order of completed) {
    for (const line of ownLines(order, ids)) {
      const lineRevenue = line.quantity * line.unitPrice;
      grossRevenue += lineRevenue;
      const prev = productMap.get(line.productId) ?? {
        quantity: 0,
        revenue: 0,
      };
      productMap.set(line.productId, {
        quantity: prev.quantity + line.quantity,
        revenue: prev.revenue + lineRevenue,
      });
    }
  }

  const fee = Math.round(grossRevenue * PLATFORM_FEE_RATE);
  const netPayout = grossRevenue - fee;
  const byProduct = Array.from(productMap.entries())
    .map(([productId, v]) => ({ productId, ...v }))
    .sort((a, b) => b.revenue - a.revenue);

  return { grossRevenue, fee, netPayout, byProduct };
}
