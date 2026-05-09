"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { StatusBadge } from "@/components/erp/status-badge";
import { fmtDate, fmtKRW, fmtN } from "@/lib/format";
import { lookupProduct } from "@/lib/mock";
import type { PurchaseOrder } from "@/lib/types";
import { cn } from "@/lib/utils";

export type SortKey = "id" | "issuedAt" | "quantity";
export type SortDir = "asc" | "desc";

interface Props {
  rows: PurchaseOrder[];
  sortKey: SortKey;
  sortDir: SortDir;
  onSortToggle: (key: SortKey) => void;
  onRowClick: (poId: number) => void;
  onReceive: (poId: number) => void;
}

export function PurchaseOrdersTableBody({
  rows,
  sortKey,
  sortDir,
  onSortToggle,
  onRowClick,
  onReceive,
}: Props) {
  const t = useTranslations("Purchase");

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
            <th className="px-4 py-2.5">{t("columns.supplier")}</th>
            <th className="px-4 py-2.5">{t("columns.product")}</th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 text-right hover:text-text-2"
              onClick={() => onSortToggle("quantity")}
            >
              {t("columns.received")}
              <SortIndicator active={sortKey === "quantity"} />
            </th>
            <th className="px-4 py-2.5 text-right">{t("columns.unitPrice")}</th>
            <th className="px-4 py-2.5 text-right">{t("columns.total")}</th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 hover:text-text-2"
              onClick={() => onSortToggle("issuedAt")}
            >
              {t("columns.issuedAt")}
              <SortIndicator active={sortKey === "issuedAt"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.status")}</th>
            <th className="w-28 px-4 py-2.5" />
          </tr>
        </thead>
        <tbody>
          {rows.map((p) => {
            const prod = lookupProduct(p.productId);
            const total = p.quantity * p.unitPrice;
            const received = p.received ?? 0;
            const open =
              p.status === "ISSUED" || p.status === "PARTIAL";
            return (
              <tr
                key={p.id}
                className={cn(
                  "cursor-pointer border-b border-divider transition-colors hover:bg-row-hover"
                )}
                onClick={() => onRowClick(p.id)}
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  PO-{p.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {p.supplier}
                </td>
                <td className="px-4 py-3 text-text-2">
                  <div>{prod.name}</div>
                  <div className="font-mono text-[11px] text-text-3">
                    {prod.sku}
                  </div>
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  <span className="font-medium text-text">
                    {fmtN(received)}
                  </span>
                  <span className="text-text-3"> / {fmtN(p.quantity)}</span>
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-3">
                  {fmtKRW(p.unitPrice)}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(total)}
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDate(p.issuedAt)}
                </td>
                <td className="px-4 py-3">
                  <StatusBadge status={p.status} />
                </td>
                <td
                  className="px-4 py-3 text-right"
                  onClick={(e) => e.stopPropagation()}
                >
                  {open ? (
                    <Button size="sm" onClick={() => onReceive(p.id)}>
                      {t("receive")}
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
              <td colSpan={9} className="px-4 py-10 text-center text-text-3">
                {t("empty")}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
