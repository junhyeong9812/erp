"use client";

import { useMemo } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { StatusBadge } from "@/components/erp/status-badge";
import { lookupCustomer, lookupWarehouse } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import {
  sellerShipments,
  shipmentSellerItems,
} from "./helpers";

interface Props {
  sellerId: number;
}

export function SellerShipmentsView({ sellerId }: Props) {
  const t = useTranslations("Seller.shipments");

  const { orders, shipments, deliveries } = useErpStore(
    useShallow((s) => ({
      orders: s.orders,
      shipments: s.shipments,
      deliveries: s.deliveries,
    }))
  );

  const rows = useMemo(() => {
    const myShipments = sellerShipments(sellerId, shipments, orders);
    const deliveryByShipmentId = new Map(
      deliveries.map((d) => [d.shipmentId, d])
    );
    return myShipments.map((s) => ({
      shipment: s,
      delivery: deliveryByShipmentId.get(s.id) ?? null,
      myItems: shipmentSellerItems(s, sellerId, orders),
    }));
  }, [orders, shipments, deliveries, sellerId]);

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("columns.id")}</th>
            <th className="px-4 py-2.5">{t("columns.customer")}</th>
            <th className="px-4 py-2.5">{t("columns.myItems")}</th>
            <th className="px-4 py-2.5">{t("columns.warehouse")}</th>
            <th className="px-4 py-2.5">{t("columns.region")}</th>
            <th className="px-4 py-2.5">{t("columns.status")}</th>
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr>
              <td colSpan={6} className="px-4 py-10 text-center text-text-3">
                {t("empty")}
              </td>
            </tr>
          ) : (
            rows.map((r) => {
              const cust = lookupCustomer(r.shipment.customerId);
              const wh = lookupWarehouse(r.shipment.warehouseId);
              return (
                <tr
                  key={r.shipment.id}
                  className="border-b border-divider transition-colors hover:bg-row-hover"
                >
                  <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                    SHP-{r.shipment.id}
                  </td>
                  <td className="px-4 py-3 font-medium text-text">
                    {cust.name}
                  </td>
                  <td className="px-4 py-3 tabular-nums text-text-2">
                    {r.myItems}개
                  </td>
                  <td className="px-4 py-3 text-text-2">{wh.name}</td>
                  <td className="px-4 py-3 text-text-2">
                    {r.delivery?.region ?? "—"}
                  </td>
                  <td className="px-4 py-3">
                    <StatusBadge
                      status={r.shipment.status}
                      live={r.shipment.status === "DISPATCHED"}
                    />
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>
  );
}
