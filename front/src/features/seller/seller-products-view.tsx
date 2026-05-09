"use client";

import { useMemo } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { Badge } from "@/components/ui/badge";
import { fmtN } from "@/lib/format";
import { lookupWarehouse, WAREHOUSES } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import { severityOf } from "@/features/stocks/severity";
import {
  sellerProducts,
  sellerStocks,
  sellerShipments,
  sellerProductIdSet,
} from "./helpers";

interface Props {
  sellerId: number;
}

export function SellerProductsView({ sellerId }: Props) {
  const t = useTranslations("Seller.products");

  const { stocks, shipments, deliveries, orders } = useErpStore(
    useShallow((s) => ({
      stocks: s.stocks,
      shipments: s.shipments,
      deliveries: s.deliveries,
      orders: s.orders,
    }))
  );

  const rows = useMemo(() => {
    const myProducts = sellerProducts(sellerId);
    const myStocks = sellerStocks(sellerId, stocks);
    const ids = sellerProductIdSet(sellerId);
    const myShipments = sellerShipments(sellerId, shipments, orders);
    const activeShipments = myShipments.filter(
      (s) => s.status === "PREPARING" || s.status === "DISPATCHED"
    );
    const orderById = new Map(orders.map((o) => [o.id, o]));
    const deliveryByShipmentId = new Map(
      deliveries.map((d) => [d.shipmentId, d])
    );

    return myProducts.map((p) => {
      const productStocks = myStocks.filter((s) => s.productId === p.id);
      const totalAvailable = productStocks.reduce(
        (sum, s) => sum + (s.total - s.reserved),
        0
      );
      const productTotal = productStocks.reduce(
        (sum, s) => sum + s.total,
        0
      );
      const severity = severityOf(totalAvailable);

      // Shipments containing this product
      const shippingTo: string[] = [];
      for (const sh of activeShipments) {
        const order = orderById.get(sh.orderId);
        if (!order) continue;
        const hasMyProduct = order.lines.some(
          (l) => l.productId === p.id && ids.has(l.productId)
        );
        if (!hasMyProduct) continue;
        const delivery = deliveryByShipmentId.get(sh.id);
        const region = delivery?.region ?? "준비중";
        const wh = lookupWarehouse(sh.warehouseId).name;
        shippingTo.push(`${wh} → ${region}`);
      }

      return {
        product: p,
        productStocks,
        productTotal,
        totalAvailable,
        severity,
        shippingTo,
      };
    });
  }, [stocks, shipments, deliveries, orders, sellerId]);

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("columns.product")}</th>
            <th className="px-4 py-2.5">{t("columns.warehouseStocks")}</th>
            <th className="px-4 py-2.5 text-right">
              {t("columns.available")}
            </th>
            <th className="px-4 py-2.5">{t("columns.shippingTo")}</th>
            <th className="px-4 py-2.5">{t("columns.severity")}</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r) => (
            <tr
              key={r.product.id}
              className="border-b border-divider transition-colors hover:bg-row-hover"
            >
              <td className="px-4 py-3">
                <div className="font-medium text-text">{r.product.name}</div>
                <div className="font-mono text-[11px] text-text-3">
                  {r.product.sku}
                </div>
              </td>
              <td className="px-4 py-3">
                <div className="flex flex-wrap gap-1.5">
                  {WAREHOUSES.map((w) => {
                    const stock = r.productStocks.find(
                      (s) => s.warehouseId === w.id
                    );
                    if (!stock) return null;
                    const avail = stock.total - stock.reserved;
                    const sev = severityOf(avail);
                    return (
                      <span
                        key={w.id}
                        className="inline-flex items-center gap-1 rounded border border-border bg-bg-elev px-2 py-0.5 text-[11px]"
                      >
                        <span className="text-text-3">{w.name}</span>
                        <span
                          className={
                            sev === "critical"
                              ? "font-medium tabular-nums text-danger"
                              : sev === "warning"
                                ? "font-medium tabular-nums text-warn"
                                : "font-medium tabular-nums text-text-2"
                          }
                        >
                          {fmtN(avail)}/{fmtN(stock.total)}
                        </span>
                      </span>
                    );
                  })}
                </div>
              </td>
              <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                {fmtN(r.totalAvailable)}
              </td>
              <td className="px-4 py-3 text-[12px] text-text-2">
                {r.shippingTo.length === 0 ? (
                  <span className="text-text-3">{t("noShipping")}</span>
                ) : (
                  <div className="flex flex-col gap-0.5">
                    <span className="text-[11px] font-medium text-text-2">
                      {t("shippingCount", { count: r.shippingTo.length })}
                    </span>
                    {r.shippingTo.map((s, i) => (
                      <span key={i} className="text-text-3">
                        {s}
                      </span>
                    ))}
                  </div>
                )}
              </td>
              <td className="px-4 py-3">
                <Badge
                  tone={
                    r.severity === "critical"
                      ? "danger"
                      : r.severity === "warning"
                        ? "warn"
                        : "ok"
                  }
                >
                  {r.severity === "critical"
                    ? "위험"
                    : r.severity === "warning"
                      ? "주의"
                      : "정상"}
                </Badge>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
