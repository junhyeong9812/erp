import { create } from "zustand";
import {
  initialOrders,
  initialShipments,
  initialDeliveries,
  initialStocks,
  initialPurchaseOrders,
  lookupCustomer,
} from "@/lib/mock";
import type {
  Order,
  Shipment,
  Delivery,
  Stock,
  PurchaseOrder,
} from "@/lib/types";

type Result<T = number> =
  | { ok: true; value?: T }
  | { ok: false; reason: string };

export interface OrderCreateInput {
  customerId: number;
  lines: Array<{ productId: number; quantity: number; unitPrice: number }>;
}

interface ErpState {
  orders: Order[];
  shipments: Shipment[];
  deliveries: Delivery[];
  stocks: Stock[];
  purchaseOrders: PurchaseOrder[];

  openOrderId: number | null;
  openShipmentId: number | null;
  openDeliveryId: number | null;
  openPurchaseOrderId: number | null;
  orderCreateOpen: boolean;

  payOrder: (orderId: number) => Result;
  dispatchShipment: (shipmentId: number) => Result;
  completeDelivery: (deliveryId: number) => Result;
  receivePurchaseOrder: (poId: number) => Result;
  createOrder: (input: OrderCreateInput) => Result<number>;

  openOrder: (orderId: number) => void;
  closeOrder: () => void;
  openShipment: (shipmentId: number) => void;
  closeShipment: () => void;
  openDelivery: (deliveryId: number) => void;
  closeDelivery: () => void;
  openPurchaseOrder: (poId: number) => void;
  closePurchaseOrder: () => void;
  openOrderCreate: () => void;
  closeOrderCreate: () => void;
}

const REGION_BY_GRADE: Record<string, string> = {
  VIP: "서울",
  GOLD: "경기",
  SILVER: "대전",
  NORMAL: "대구",
};

const DEFAULT_RECEIVE_WAREHOUSE_ID = 1;

export const useErpStore = create<ErpState>((set, get) => ({
  orders: [...initialOrders],
  shipments: [...initialShipments],
  deliveries: [...initialDeliveries],
  stocks: [...initialStocks],
  purchaseOrders: [...initialPurchaseOrders],

  openOrderId: null,
  openShipmentId: null,
  openDeliveryId: null,
  openPurchaseOrderId: null,
  orderCreateOpen: false,

  openOrder: (id) => set({ openOrderId: id }),
  closeOrder: () => set({ openOrderId: null }),
  openShipment: (id) => set({ openShipmentId: id }),
  closeShipment: () => set({ openShipmentId: null }),
  openDelivery: (id) => set({ openDeliveryId: id }),
  closeDelivery: () => set({ openDeliveryId: null }),
  openPurchaseOrder: (id) => set({ openPurchaseOrderId: id }),
  closePurchaseOrder: () => set({ openPurchaseOrderId: null }),
  openOrderCreate: () => set({ orderCreateOpen: true }),
  closeOrderCreate: () => set({ orderCreateOpen: false }),

  createOrder: (input) => {
    const state = get();
    if (input.lines.length === 0) {
      return { ok: false, reason: "EMPTY_LINES" };
    }
    const newId = Math.max(0, ...state.orders.map((o) => o.id)) + 1;
    const total = input.lines.reduce(
      (sum, l) => sum + l.quantity * l.unitPrice,
      0
    );
    const newOrder: Order = {
      id: newId,
      customerId: input.customerId,
      status: "PLACED",
      placedAt: new Date().toISOString(),
      lines: input.lines.map((l) => ({
        productId: l.productId,
        quantity: l.quantity,
        unitPrice: l.unitPrice,
      })),
      total,
      paid: false,
      shipmentId: null,
    };
    set({ orders: [newOrder, ...state.orders] });
    return { ok: true, value: newId };
  },

  payOrder: (orderId) => {
    const state = get();
    const order = state.orders.find((o) => o.id === orderId);
    if (!order) return { ok: false, reason: "ORDER_NOT_FOUND" };
    if (order.paid) return { ok: false, reason: "ALREADY_PAID" };

    const newShipmentId =
      Math.max(0, ...state.shipments.map((s) => s.id)) + 1;
    const totalItems = order.lines.reduce((s, l) => s + l.quantity, 0);
    const newShipment: Shipment = {
      id: newShipmentId,
      orderId,
      customerId: order.customerId,
      status: "PREPARING",
      warehouseId: 1,
      items: totalItems,
      weightKg: Math.round(totalItems * 1.5),
      driver: null,
      tracking: null,
      preparedAt: new Date().toISOString(),
      dispatchedAt: null,
      deliveredAt: null,
    };

    set({
      orders: state.orders.map((o) =>
        o.id === orderId
          ? { ...o, paid: true, status: "CONFIRMED", shipmentId: newShipmentId }
          : o
      ),
      shipments: [...state.shipments, newShipment],
    });
    return { ok: true };
  },

  dispatchShipment: (shipmentId) => {
    const state = get();
    const shipment = state.shipments.find((s) => s.id === shipmentId);
    if (!shipment) return { ok: false, reason: "SHIPMENT_NOT_FOUND" };
    if (shipment.status !== "PREPARING") {
      return { ok: false, reason: "NOT_PREPARING" };
    }

    const now = new Date();
    const dateTag = now.toISOString().slice(0, 10).replace(/-/g, "");
    const tracking = `TRK-${dateTag}${String(shipmentId).padStart(4, "0")}`;
    const driver = `DRV-${String(shipmentId).padStart(3, "0")}`;
    const eta = new Date(now.getTime() + 4 * 60 * 60 * 1000).toISOString();

    const cust = lookupCustomer(shipment.customerId);
    const region = REGION_BY_GRADE[cust.grade] ?? "기타";

    const newDeliveryId =
      Math.max(0, ...state.deliveries.map((d) => d.id)) + 1;
    const newDelivery: Delivery = {
      id: newDeliveryId,
      shipmentId,
      orderId: shipment.orderId,
      customerId: shipment.customerId,
      status: "ASSIGNED",
      eta,
      deliveredAt: null,
      courier: "CJ대한통운",
      driver,
      region,
    };

    set({
      shipments: state.shipments.map((s) =>
        s.id === shipmentId
          ? { ...s, status: "DISPATCHED", driver, tracking, dispatchedAt: now.toISOString() }
          : s
      ),
      deliveries: [...state.deliveries, newDelivery],
      orders: state.orders.map((o) =>
        o.shipmentId === shipmentId && o.status === "CONFIRMED"
          ? { ...o, status: "SHIPPED" }
          : o
      ),
    });
    return { ok: true };
  },

  completeDelivery: (deliveryId) => {
    const state = get();
    const delivery = state.deliveries.find((d) => d.id === deliveryId);
    if (!delivery) return { ok: false, reason: "DELIVERY_NOT_FOUND" };
    if (delivery.status === "DELIVERED") {
      return { ok: false, reason: "ALREADY_DELIVERED" };
    }

    const now = new Date().toISOString();

    set({
      deliveries: state.deliveries.map((d) =>
        d.id === deliveryId
          ? { ...d, status: "DELIVERED", deliveredAt: now }
          : d
      ),
      shipments: state.shipments.map((s) =>
        s.id === delivery.shipmentId
          ? { ...s, status: "COMPLETED", deliveredAt: now }
          : s
      ),
      orders: state.orders.map((o) =>
        o.id === delivery.orderId && o.status !== "COMPLETED"
          ? { ...o, status: "COMPLETED" }
          : o
      ),
    });
    return { ok: true };
  },

  receivePurchaseOrder: (poId) => {
    const state = get();
    const po = state.purchaseOrders.find((p) => p.id === poId);
    if (!po) return { ok: false, reason: "PO_NOT_FOUND" };
    if (po.status === "COMPLETED" || po.status === "CANCELLED") {
      return { ok: false, reason: "ALREADY_FINALIZED" };
    }

    const remaining = po.quantity - (po.received ?? 0);
    if (remaining <= 0) return { ok: false, reason: "NOTHING_TO_RECEIVE" };

    const targetWarehouseId = DEFAULT_RECEIVE_WAREHOUSE_ID;
    const existing = state.stocks.find(
      (s) =>
        s.productId === po.productId && s.warehouseId === targetWarehouseId
    );

    const nextStocks: Stock[] = existing
      ? state.stocks.map((s) =>
          s.productId === po.productId &&
          s.warehouseId === targetWarehouseId
            ? { ...s, total: s.total + remaining }
            : s
        )
      : [
          ...state.stocks,
          {
            productId: po.productId,
            warehouseId: targetWarehouseId,
            total: remaining,
            reserved: 0,
          },
        ];

    set({
      purchaseOrders: state.purchaseOrders.map((p) =>
        p.id === poId
          ? { ...p, status: "COMPLETED", received: po.quantity }
          : p
      ),
      stocks: nextStocks,
    });
    return { ok: true };
  },
}));
