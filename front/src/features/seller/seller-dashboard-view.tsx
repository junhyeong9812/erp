"use client";

import { useMemo } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { Kpi } from "@/components/erp/kpi";
import { StatusBadge } from "@/components/erp/status-badge";
import { Stepper } from "@/components/erp/stepper";
import { fmtKRW, fmtN } from "@/lib/format";
import {
  lookupCustomer,
  lookupWarehouse,
} from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import { SHIPMENT_STEPS } from "@/lib/types";
import {
  sellerProducts,
  sellerOrders,
  sellerShipments,
  shipmentSellerItems,
  sellerProductIdSet,
  sellerOrderAmount,
} from "./helpers";

interface Props {
  sellerId: number;
}

export function SellerDashboardView({ sellerId }: Props) {
  const t = useTranslations("Seller.dashboard");

  const { orders, shipments, deliveries } = useErpStore(
    useShallow((s) => ({
      orders: s.orders,
      shipments: s.shipments,
      deliveries: s.deliveries,
    }))
  );

  const { mineProducts, openOrdersCount, activeShipmentsList, estRevenue } =
    useMemo(() => {
      const ids = sellerProductIdSet(sellerId);
      const myProducts = sellerProducts(sellerId);
      const myOrders = sellerOrders(sellerId, orders);
      const open = myOrders.filter(
        (o) => o.status === "PLACED" || o.status === "CONFIRMED"
      );
      const myShipments = sellerShipments(sellerId, shipments, orders);
      const active = myShipments.filter(
        (s) => s.status === "PREPARING" || s.status === "DISPATCHED"
      );
      const today = "2026-05-04";
      const todays = myOrders.filter((o) => o.placedAt.startsWith(today));
      const est = todays.reduce(
        (sum, o) => sum + sellerOrderAmount(o, ids),
        0
      );
      return {
        mineProducts: myProducts,
        openOrdersCount: open.length,
        activeShipmentsList: active,
        estRevenue: est,
      };
    }, [orders, shipments, sellerId]);

  const orderById = new Map(orders.map((o) => [o.id, o]));
  const deliveryByShipmentId = new Map(
    deliveries.map((d) => [d.shipmentId, d])
  );

  return (
    <>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <Kpi
          label={t("kpiMyProducts")}
          value={mineProducts.length}
          unit={t("unitSku")}
          spark={[3, 3, 3, 3, 3, 3, 3, 3, mineProducts.length]}
        />
        <Kpi
          label={t("kpiOpenOrders")}
          value={openOrdersCount}
          unit={t("unitOrders")}
          delta={openOrdersCount === 0 ? 0 : 12.0}
          spark={[1, 2, 1, 2, 3, 2, 3, 3, openOrdersCount]}
        />
        <Kpi
          label={t("kpiActiveShipments")}
          value={activeShipmentsList.length}
          unit={t("unitShipments")}
          spark={[1, 2, 2, 1, 2, 3, 2, 1, activeShipmentsList.length]}
        />
        <Kpi
          label={t("kpiEstRevenue")}
          value={estRevenue}
          format="krw"
          delta={estRevenue > 0 ? 8.4 : 0}
          spark={[10, 12, 14, 12, 16, 18, 20, 18, estRevenue / 1_000_000]}
        />
      </div>

      <section className="mt-6 rounded-lg border border-border bg-panel p-5">
        <h2 className="mb-4 text-[13px] font-semibold text-text">
          {t("activeShipmentsTitle")}
        </h2>
        {activeShipmentsList.length === 0 ? (
          <div className="rounded-md border border-dashed border-border p-8 text-center text-[13px] text-text-3">
            {t("noActiveShipments")}
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            {activeShipmentsList.map((s) => {
              const order = orderById.get(s.orderId);
              if (!order) return null;
              const cust = lookupCustomer(s.customerId);
              const wh = lookupWarehouse(s.warehouseId);
              const delivery = deliveryByShipmentId.get(s.id);
              const myItems = shipmentSellerItems(s, sellerId, orders);
              return (
                <div
                  key={s.id}
                  className="rounded-md border border-border-2 bg-panel-2 p-3"
                >
                  <div className="mb-3 flex items-center justify-between">
                    <div className="flex items-center gap-2.5">
                      <span className="font-mono text-[11.5px] text-text-3">
                        SHP-{s.id}
                      </span>
                      <span className="text-[13px] font-medium text-text">
                        {cust.name}
                      </span>
                      <span className="text-[12px] text-text-3">
                        ·{" "}
                        {t("fromTo", {
                          from: wh.name,
                          region: delivery?.region ?? "—",
                        })}
                      </span>
                      <span className="text-[12px] text-text-3">
                        · 내 품목 {fmtN(myItems)}
                      </span>
                    </div>
                    <StatusBadge
                      status={s.status}
                      live={s.status === "DISPATCHED"}
                    />
                  </div>
                  <Stepper steps={SHIPMENT_STEPS} current={s.status} />
                  {delivery && (
                    <div className="mt-2 flex items-center gap-2 text-[11.5px] text-text-3">
                      <span className="font-mono">DLV-{delivery.id}</span>
                      <span>· {delivery.courier}</span>
                      <span>· {delivery.driver}</span>
                      <span>· ETA {fmtKRW(0).replace("₩0", "")} </span>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </section>
    </>
  );
}
