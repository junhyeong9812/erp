import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW, fmtDateTime } from "@/lib/format";
import { lookupSeller } from "@/lib/mock";
import { SELLER_SETTLEMENTS, lookupPeriod } from "@/lib/mock/settlement";

export async function SellersView() {
  const t = await getTranslations("Settlement.sellers");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.seller")}</th>
            <th className="px-4 py-2.5">{t("col.period")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.gross")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.refund")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.fee")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.net")}</th>
            <th className="px-4 py-2.5">{t("col.paidAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {SELLER_SETTLEMENTS.map((s) => {
            const seller = lookupSeller(s.sellerId);
            const period = lookupPeriod(s.periodId);
            return (
              <tr
                key={s.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  SS-{s.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {seller.name}
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {period.startDate.slice(0, 7)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtKRW(s.grossSales)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-danger">
                  −{fmtKRW(s.refundAmount)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-warn">
                  −{fmtKRW(s.feeAmount)}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(s.netPayout)}
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {s.paidAt ? fmtDateTime(s.paidAt) : "—"}
                </td>
                <td className="px-4 py-3">
                  <Badge tone={s.status === "PAID" ? "ok" : "warn"}>
                    {t(`status.${s.status}`)}
                  </Badge>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
