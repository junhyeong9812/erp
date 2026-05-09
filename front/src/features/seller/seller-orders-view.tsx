"use client";

import { useMemo } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { StatusBadge } from "@/components/erp/status-badge";
import { fmtKRW, fmtDateTime } from "@/lib/format";
import { lookupCustomer, lookupProduct } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import {
  sellerOrders,
  sellerOrderAmount,
  sellerOrderItems,
  sellerProductIdSet,
  ownLines,
} from "./helpers";

interface Props {
  sellerId: number;
}

export function SellerOrdersView({ sellerId }: Props) {
  const t = useTranslations("Seller.orders");
  const orders = useErpStore(useShallow((s) => s.orders));

  const rows = useMemo(() => {
    const ids = sellerProductIdSet(sellerId);
    return sellerOrders(sellerId, orders).map((o) => {
      const myLines = ownLines(o, ids);
      return {
        order: o,
        myLines,
        myAmount: sellerOrderAmount(o, ids),
        myItems: sellerOrderItems(o, ids),
      };
    });
  }, [orders, sellerId]);

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("columns.id")}</th>
            <th className="px-4 py-2.5">{t("columns.customer")}</th>
            <th className="px-4 py-2.5">{t("columns.myItems")}</th>
            <th className="px-4 py-2.5 text-right">{t("columns.myAmount")}</th>
            <th className="px-4 py-2.5">{t("columns.placedAt")}</th>
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
              const cust = lookupCustomer(r.order.customerId);
              const firstLine = r.myLines[0];
              const firstName = firstLine
                ? lookupProduct(firstLine.productId).name
                : "—";
              const moreCount = r.myLines.length - 1;
              return (
                <tr
                  key={r.order.id}
                  className="border-b border-divider transition-colors hover:bg-row-hover"
                >
                  <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                    #{r.order.id}
                  </td>
                  <td className="px-4 py-3 font-medium text-text">
                    {cust.name}
                  </td>
                  <td className="px-4 py-3 text-text-2">
                    <div>{firstName}</div>
                    {moreCount > 0 && (
                      <div className="text-[11px] text-text-3">
                        외 {moreCount}품목
                      </div>
                    )}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <div className="font-medium tabular-nums text-text">
                      {fmtKRW(r.myAmount)}
                    </div>
                    <div
                      className="text-[10.5px] text-text-3"
                      title={t("myAmountHint")}
                    >
                      {r.myItems}개
                    </div>
                  </td>
                  <td className="px-4 py-3 text-text-2 tabular-nums">
                    {fmtDateTime(r.order.placedAt)}
                  </td>
                  <td className="px-4 py-3">
                    <StatusBadge status={r.order.status} />
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
