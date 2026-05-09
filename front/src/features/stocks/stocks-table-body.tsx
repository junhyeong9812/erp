"use client";

import { useTranslations } from "next-intl";
import { Badge } from "@/components/ui/badge";
import { fmtN } from "@/lib/format";
import { lookupProduct, lookupWarehouse } from "@/lib/mock";
import type { Stock } from "@/lib/types";
import type { StockSeverity } from "./severity";
import { cn } from "@/lib/utils";

export type SortKey = "product" | "total" | "reserved" | "available";
export type SortDir = "asc" | "desc";

interface Row extends Stock {
  available: number;
  severity: StockSeverity;
}

interface Props {
  rows: Row[];
  sortKey: SortKey;
  sortDir: SortDir;
  onSortToggle: (key: SortKey) => void;
}

export function StocksTableBody({
  rows,
  sortKey,
  sortDir,
  onSortToggle,
}: Props) {
  const t = useTranslations("Stocks");

  const SortIndicator = ({ active }: { active: boolean }) =>
    active ? (
      <span className="ml-1 text-[10px] text-text-3">
        {sortDir === "asc" ? "↑" : "↓"}
      </span>
    ) : null;

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th
              className="cursor-pointer select-none px-4 py-2.5 hover:text-text-2"
              onClick={() => onSortToggle("product")}
            >
              {t("columns.product")}
              <SortIndicator active={sortKey === "product"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.warehouse")}</th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 text-right hover:text-text-2"
              onClick={() => onSortToggle("total")}
            >
              {t("columns.total")}
              <SortIndicator active={sortKey === "total"} />
            </th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 text-right hover:text-text-2"
              onClick={() => onSortToggle("reserved")}
            >
              {t("columns.reserved")}
              <SortIndicator active={sortKey === "reserved"} />
            </th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 text-right hover:text-text-2"
              onClick={() => onSortToggle("available")}
            >
              {t("columns.available")}
              <SortIndicator active={sortKey === "available"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.severity")}</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((s) => {
            const p = lookupProduct(s.productId);
            const w = lookupWarehouse(s.warehouseId);
            return (
              <tr
                key={`${s.productId}-${s.warehouseId}`}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3">
                  <div className="font-medium text-text">{p.name}</div>
                  <div className="font-mono text-[11px] text-text-3">
                    {p.sku}
                  </div>
                </td>
                <td className="px-4 py-3 text-text-2">{w.name}</td>
                <td className="px-4 py-3 text-right tabular-nums text-text">
                  {fmtN(s.total)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtN(s.reserved)}
                </td>
                <td
                  className={cn(
                    "px-4 py-3 text-right font-medium tabular-nums",
                    s.severity === "critical" && "text-danger",
                    s.severity === "warning" && "text-warn",
                    s.severity === "normal" && "text-text"
                  )}
                >
                  {fmtN(s.available)}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      s.severity === "critical"
                        ? "danger"
                        : s.severity === "warning"
                          ? "warn"
                          : "ok"
                    }
                  >
                    {t(`severityBadge.${s.severity}`)}
                  </Badge>
                </td>
              </tr>
            );
          })}
          {rows.length === 0 && (
            <tr>
              <td colSpan={6} className="px-4 py-10 text-center text-text-3">
                {t("empty")}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
