"use client";

import { useTranslations } from "next-intl";
import { Icon } from "@/components/icons";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import type { DeliveryStatus } from "@/lib/types";

export type DeliveryFilterKey = "ALL" | DeliveryStatus;

interface Props {
  filter: DeliveryFilterKey;
  onFilterChange: (next: DeliveryFilterKey) => void;
  search: string;
  onSearchChange: (next: string) => void;
  counts: Record<DeliveryFilterKey, number>;
}

const FILTER_KEYS: DeliveryFilterKey[] = [
  "ALL",
  "ASSIGNED",
  "IN_TRANSIT",
  "DELIVERED",
  "RETURNED",
];

export function DeliveriesFilterBar({
  filter,
  onFilterChange,
  search,
  onSearchChange,
  counts,
}: Props) {
  const t = useTranslations("Deliveries");
  return (
    <div className="mb-3 flex flex-wrap items-center gap-3">
      <div className="flex flex-wrap items-center gap-1.5">
        {FILTER_KEYS.map((k) => {
          const active = filter === k;
          return (
            <button
              key={k}
              onClick={() => onFilterChange(k)}
              className={cn(
                "inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-[12px] transition-colors",
                active
                  ? "border-accent bg-accent-soft text-accent-ink"
                  : "border-border bg-panel text-text-2 hover:bg-hover hover:text-text"
              )}
            >
              <span>{t(`filters.${k}`)}</span>
              <span
                className={cn(
                  "rounded-full px-1.5 text-[10.5px] tabular-nums",
                  active ? "bg-accent text-white" : "bg-hover text-text-3"
                )}
              >
                {counts[k] ?? 0}
              </span>
            </button>
          );
        })}
      </div>
      <div className="ml-auto flex w-full max-w-xs items-center gap-2">
        <div className="flex w-full items-center gap-2 rounded-md border border-border bg-bg-elev px-3">
          <Icon.Search className="size-3.5 text-text-3" />
          <Input
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder={t("searchPlaceholder")}
            className="h-9 border-0 bg-transparent px-0 focus-visible:ring-0"
          />
        </div>
      </div>
    </div>
  );
}
