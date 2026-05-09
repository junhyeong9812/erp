"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { StatusBadge } from "@/components/erp/status-badge";
import { fmtKRW, fmtDateTime } from "@/lib/format";
import { lookupCustomer, lookupProduct } from "@/lib/mock";
import type { Order } from "@/lib/types";
import { cn } from "@/lib/utils";

export type SortKey = "id" | "total" | "placedAt";
export type SortDir = "asc" | "desc";

interface Props {
  rows: Order[];
  sortKey: SortKey;
  sortDir: SortDir;
  onSortToggle: (key: SortKey) => void;
  onRowClick: (orderId: number) => void;
  onPay: (orderId: number) => void;
}

export function OrdersTableBody({
  rows,
  sortKey,
  sortDir,
  onSortToggle,
  onRowClick,
  onPay,
}: Props) {
  const t = useTranslations("Orders");

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
              onClick={() => onSortToggle("id")}
            >
              {t("columns.id")}
              <SortIndicator active={sortKey === "id"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.customer")}</th>
            <th className="px-4 py-2.5">{t("columns.items")}</th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 text-right hover:text-text-2"
              onClick={() => onSortToggle("total")}
            >
              {t("columns.amount")}
              <SortIndicator active={sortKey === "total"} />
            </th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 hover:text-text-2"
              onClick={() => onSortToggle("placedAt")}
            >
              {t("columns.placedAt")}
              <SortIndicator active={sortKey === "placedAt"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.status")}</th>
            <th className="w-24 px-4 py-2.5" />
          </tr>
        </thead>
        <tbody>
          {rows.map((o) => {
            const cust = lookupCustomer(o.customerId);
            const firstLine = o.lines[0];
            const firstName = firstLine
              ? lookupProduct(firstLine.productId).name
              : "—";
            const moreCount = o.lines.length - 1;
            return (
              <tr
                key={o.id}
                className={cn(
                  "cursor-pointer border-b border-divider transition-colors",
                  "hover:bg-row-hover"
                )}
                onClick={() => onRowClick(o.id)}
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  #{o.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3 text-text-2">
                  {firstName}
                  {moreCount > 0 && (
                    <span className="ml-1 text-text-3">
                      {t("andMore", { count: moreCount })}
                    </span>
                  )}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(o.total)}
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(o.placedAt)}
                </td>
                <td className="px-4 py-3">
                  <StatusBadge status={o.status} />
                </td>
                <td
                  className="px-4 py-3 text-right"
                  onClick={(e) => e.stopPropagation()}
                >
                  {!o.paid ? (
                    <Button
                      variant="default"
                      size="sm"
                      onClick={() => onPay(o.id)}
                    >
                      {t("pay")}
                    </Button>
                  ) : (
                    <Button variant="ghost" size="icon" aria-label="more">
                      <Icon.More className="size-4" />
                    </Button>
                  )}
                </td>
              </tr>
            );
          })}
          {rows.length === 0 && (
            <tr>
              <td
                colSpan={7}
                className="px-4 py-10 text-center text-text-3"
              >
                {t("empty")}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
