"use client";

import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import { StatusBadge } from "@/components/erp/status-badge";
import { fmtDateTime } from "@/lib/format";
import { lookupCustomer } from "@/lib/mock";
import type { Delivery } from "@/lib/types";
import { cn } from "@/lib/utils";

export type SortKey = "id" | "eta";
export type SortDir = "asc" | "desc";

interface Props {
  rows: Delivery[];
  sortKey: SortKey;
  sortDir: SortDir;
  onSortToggle: (key: SortKey) => void;
  onRowClick: (deliveryId: number) => void;
  onComplete: (deliveryId: number) => void;
}

export function DeliveriesTableBody({
  rows,
  sortKey,
  sortDir,
  onSortToggle,
  onRowClick,
  onComplete,
}: Props) {
  const t = useTranslations("Deliveries");

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
            <th className="px-4 py-2.5">{t("columns.shipment")}</th>
            <th className="px-4 py-2.5">{t("columns.customer")}</th>
            <th className="px-4 py-2.5">{t("columns.courier")}</th>
            <th className="px-4 py-2.5">{t("columns.region")}</th>
            <th
              className="cursor-pointer select-none px-4 py-2.5 hover:text-text-2"
              onClick={() => onSortToggle("eta")}
            >
              {t("columns.eta")}
              <SortIndicator active={sortKey === "eta"} />
            </th>
            <th className="px-4 py-2.5">{t("columns.status")}</th>
            <th className="w-28 px-4 py-2.5" />
          </tr>
        </thead>
        <tbody>
          {rows.map((d) => {
            const cust = lookupCustomer(d.customerId);
            const inFlight =
              d.status === "ASSIGNED" || d.status === "IN_TRANSIT";
            return (
              <tr
                key={d.id}
                className={cn(
                  "cursor-pointer border-b border-divider transition-colors hover:bg-row-hover"
                )}
                onClick={() => onRowClick(d.id)}
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  DLV-{d.id}
                </td>
                <td className="px-4 py-3 font-mono text-[12px] text-text-3">
                  SHP-{d.shipmentId}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3 text-text-2">{d.courier}</td>
                <td className="px-4 py-3 text-text-2">{d.region}</td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(d.eta)}
                </td>
                <td className="px-4 py-3">
                  <StatusBadge
                    status={d.status}
                    live={d.status === "IN_TRANSIT"}
                  />
                </td>
                <td
                  className="px-4 py-3 text-right"
                  onClick={(e) => e.stopPropagation()}
                >
                  {inFlight ? (
                    <Button size="sm" onClick={() => onComplete(d.id)}>
                      {t("complete")}
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
