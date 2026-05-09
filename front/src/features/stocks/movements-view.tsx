import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtN, fmtDateTime } from "@/lib/format";
import {
  STOCK_MOVEMENTS,
  lookupProduct,
  lookupWarehouse,
} from "@/lib/mock";

export async function MovementsView() {
  const t = await getTranslations("Stocks.movements");

  const sorted = [...STOCK_MOVEMENTS].sort((a, b) =>
    a.occurredAt < b.occurredAt ? 1 : -1
  );

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.occurredAt")}</th>
            <th className="px-4 py-2.5">{t("col.product")}</th>
            <th className="px-4 py-2.5">{t("col.warehouse")}</th>
            <th className="px-4 py-2.5">{t("col.type")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.quantity")}</th>
            <th className="px-4 py-2.5">{t("col.reference")}</th>
            <th className="px-4 py-2.5">{t("col.note")}</th>
          </tr>
        </thead>
        <tbody>
          {sorted.map((m) => {
            const p = lookupProduct(m.productId);
            const w = lookupWarehouse(m.warehouseId);
            const isOutflow =
              m.type === "OUT" ||
              m.type === "RESERVE" ||
              (m.type === "ADJUST" && m.quantity < 0);
            return (
              <tr
                key={m.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(m.occurredAt)}
                </td>
                <td className="px-4 py-3">
                  <div className="font-medium text-text">{p.name}</div>
                  <div className="font-mono text-[11px] text-text-3">
                    {p.sku}
                  </div>
                </td>
                <td className="px-4 py-3 text-text-2">{w.name}</td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      m.type === "IN"
                        ? "ok"
                        : m.type === "OUT"
                          ? "warn"
                          : m.type === "RESERVE"
                            ? "info"
                            : m.type === "RELEASE"
                              ? "neutral"
                              : m.type === "ADJUST"
                                ? "danger"
                                : "info"
                    }
                  >
                    {t(`type.${m.type}`)}
                  </Badge>
                </td>
                <td
                  className={
                    isOutflow
                      ? "px-4 py-3 text-right font-medium tabular-nums text-danger"
                      : "px-4 py-3 text-right font-medium tabular-nums text-ok"
                  }
                >
                  {isOutflow ? "−" : "+"}
                  {fmtN(Math.abs(m.quantity))}
                </td>
                <td className="px-4 py-3 font-mono text-[11.5px] text-text-3">
                  {m.reference ?? "—"}
                </td>
                <td className="px-4 py-3 text-text-2">{m.note ?? "—"}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
