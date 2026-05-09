"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { StatusBadge } from "@/components/erp/status-badge";
import { fmtDateTime, fmtN } from "@/lib/format";
import { lookupCustomer, lookupWarehouse } from "@/lib/mock";
import type { Shipment } from "@/lib/types";
import { cn } from "@/lib/utils";

export type SortKey = "id" | "preparedAt" | "items";
export type SortDir = "asc" | "desc";

interface Props {
  rows: Shipment[];
  sortKey: SortKey;
  sortDir: SortDir;
  onSortToggle: (key: SortKey) => void;
  onRowClick: (shipmentId: number) => void;
  onDispatch: (shipmentId: number) => void;
}

export function ShipmentsTableBody({
  rows,
  sortKey,
  sortDir,
  onSortToggle,
  onRowClick,
  onDispatch,
}: Props) {
  const t = useTranslations("Shipments");

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
            <th className="px-4 py-2.5">{t("columns.warehouse")}</th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 hover:text-text-2"
              onClick={() => onSortToggle("items")}
            >
              {t("columns.items")}
              <SortIndicator active={sortKey === "items"} />
            </th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 hover:text-text-2"
              onClick={() => onSortToggle("preparedAt")}
            >
              {t("columns.dispatchedAt")}
              <SortIndicator active={sortKey === "preparedAt"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.tracking")}</th>
            <th className="px-4 py-2.5">{t("columns.status")}</th>
            <th className="w-32 px-4 py-2.5" />
          </tr>
        </thead>
        <tbody>
          {rows.map((s) => {
            const cust = lookupCustomer(s.customerId);
            const wh = lookupWarehouse(s.warehouseId);
            return (
              <tr
                key={s.id}
                className={cn(
                  "cursor-pointer border-b border-divider transition-colors hover:bg-row-hover"
                )}
                onClick={() => onRowClick(s.id)}
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  SHP-{s.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3 text-text-2">{wh.name}</td>
                <td className="px-4 py-3 tabular-nums text-text-2">
                  {fmtN(s.items)} {t("itemsUnit")}{" "}
                  <span className="text-text-3">
                    · {s.weightKg}
                    {t("weightUnit")}
                  </span>
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(s.dispatchedAt ?? s.preparedAt)}
                </td>
                <td className="px-4 py-3 font-mono text-[11.5px] text-text-3">
                  {s.tracking ?? "—"}
                </td>
                <td className="px-4 py-3">
                  <StatusBadge
                    status={s.status}
                    live={s.status === "DISPATCHED"}
                  />
                </td>
                <td
                  className="px-4 py-3 text-right"
                  onClick={(e) => e.stopPropagation()}
                >
                  {s.status === "PREPARING" ? (
                    <Button size="sm" onClick={() => onDispatch(s.id)}>
                      {t("dispatch")}
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
              <td colSpan={8} className="px-4 py-10 text-center text-text-3">
                {t("empty")}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
