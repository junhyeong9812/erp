import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtN, fmtDateTime } from "@/lib/format";
import { WORK_ORDERS, lookupProduct } from "@/lib/mock";

export async function ProductionView() {
  const t = await getTranslations("Production");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.product")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.planned")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.produced")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.defective")}</th>
            <th className="px-4 py-2.5">{t("col.progress")}</th>
            <th className="px-4 py-2.5">{t("col.issuedAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {WORK_ORDERS.map((w) => {
            const p = lookupProduct(w.productId);
            const pct =
              w.plannedQuantity > 0
                ? Math.round((w.producedQuantity / w.plannedQuantity) * 100)
                : 0;
            return (
              <tr
                key={w.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  WO-{w.id}
                </td>
                <td className="px-4 py-3">
                  <div className="font-medium text-text">{p.name}</div>
                  <div className="font-mono text-[11px] text-text-3">
                    {p.sku}
                  </div>
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtN(w.plannedQuantity)}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtN(w.producedQuantity)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-danger">
                  {w.defectiveQuantity > 0 ? fmtN(w.defectiveQuantity) : "—"}
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <div className="h-1.5 w-20 overflow-hidden rounded-full bg-hover">
                      <div
                        className="h-full bg-accent"
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                    <span className="text-[11px] tabular-nums text-text-3">
                      {pct}%
                    </span>
                  </div>
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {fmtDateTime(w.issuedAt)}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      w.status === "COMPLETED"
                        ? "ok"
                        : w.status === "IN_PROGRESS"
                          ? "warn"
                          : w.status === "PLANNED"
                            ? "info"
                            : "neutral"
                    }
                    live={w.status === "IN_PROGRESS"}
                  >
                    {t(`status.${w.status}`)}
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
