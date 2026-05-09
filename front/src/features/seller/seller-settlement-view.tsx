"use client";

import { useMemo } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { Kpi } from "@/components/erp/kpi";
import { fmtKRW, fmtN } from "@/lib/format";
import { lookupProduct } from "@/lib/mock";
import { useErpStore } from "@/store/erp-store";
import { sellerSettlementSnapshot } from "./helpers";

interface Props {
  sellerId: number;
}

export function SellerSettlementView({ sellerId }: Props) {
  const t = useTranslations("Seller.settlement");
  const orders = useErpStore(useShallow((s) => s.orders));

  const snapshot = useMemo(
    () => sellerSettlementSnapshot(sellerId, orders),
    [orders, sellerId]
  );

  return (
    <>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        <Kpi
          label={t("kpiGrossRevenue")}
          value={snapshot.grossRevenue}
          format="krw"
          spark={[2, 3, 4, 3, 5, 6, 7, 6, snapshot.grossRevenue / 1_000_000]}
        />
        <Kpi
          label={t("kpiFee")}
          value={snapshot.fee}
          format="krw"
          spark={[1, 1, 2, 2, 3, 3, 4, 4, snapshot.fee / 100_000]}
        />
        <Kpi
          label={t("kpiNetPayout")}
          value={snapshot.netPayout}
          format="krw"
          delta={snapshot.netPayout > 0 ? 5.2 : 0}
          spark={[2, 3, 4, 3, 5, 6, 7, 6, snapshot.netPayout / 1_000_000]}
        />
      </div>

      <section className="mt-6 rounded-lg border border-border bg-panel p-5">
        <div className="mb-3 flex items-center justify-between">
          <h2 className="text-[13px] font-semibold text-text">
            {t("byProductTitle")}
          </h2>
          <span className="rounded-full bg-info-soft px-2 py-0.5 text-[11px] text-info-ink">
            {t("thisMonthCard")}
          </span>
        </div>
        {snapshot.byProduct.length === 0 ? (
          <div className="rounded-md border border-dashed border-border p-8 text-center text-[13px] text-text-3">
            {t("noData")}
          </div>
        ) : (
          <table className="w-full text-[13px]">
            <thead>
              <tr className="border-b border-border text-left text-[11px] uppercase tracking-wider text-text-3">
                <th className="py-1.5">{t("byProductColumns.product")}</th>
                <th className="py-1.5 text-right">
                  {t("byProductColumns.qty")}
                </th>
                <th className="py-1.5 text-right">
                  {t("byProductColumns.revenue")}
                </th>
              </tr>
            </thead>
            <tbody>
              {snapshot.byProduct.map((row) => {
                const p = lookupProduct(row.productId);
                return (
                  <tr key={row.productId} className="border-b border-divider">
                    <td className="py-2">
                      <div className="font-medium text-text">{p.name}</div>
                      <div className="font-mono text-[11px] text-text-3">
                        {p.sku}
                      </div>
                    </td>
                    <td className="py-2 text-right tabular-nums text-text-2">
                      {fmtN(row.quantity)}
                    </td>
                    <td className="py-2 text-right font-medium tabular-nums text-text">
                      {fmtKRW(row.revenue)}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </section>
    </>
  );
}
