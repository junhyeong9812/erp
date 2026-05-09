"use client";

import { useMemo, useState } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { toast } from "sonner";
import { useErpStore } from "@/store/erp-store";
import { lookupCustomer } from "@/lib/mock";
import {
  DeliveriesFilterBar,
  type DeliveryFilterKey,
} from "./deliveries-filter-bar";
import {
  DeliveriesTableBody,
  type SortDir,
  type SortKey,
} from "./deliveries-table-body";
import { DeliveryDetailSheet } from "./delivery-detail-sheet";

export function DeliveriesTable() {
  const tToast = useTranslations("Deliveries.toast");

  const { deliveries, complete, openDelivery } = useErpStore(
    useShallow((s) => ({
      deliveries: s.deliveries,
      complete: s.completeDelivery,
      openDelivery: s.openDelivery,
    }))
  );

  const [filter, setFilter] = useState<DeliveryFilterKey>("ALL");
  const [search, setSearch] = useState("");
  const [sortKey, setSortKey] = useState<SortKey>("eta");
  const [sortDir, setSortDir] = useState<SortDir>("asc");

  const counts = useMemo(() => {
    const c: Record<DeliveryFilterKey, number> = {
      ALL: deliveries.length,
      ASSIGNED: 0,
      IN_TRANSIT: 0,
      DELIVERED: 0,
      RETURNED: 0,
    };
    for (const d of deliveries) c[d.status] = (c[d.status] ?? 0) + 1;
    return c;
  }, [deliveries]);

  const filtered = useMemo(() => {
    let rs = deliveries;
    if (filter !== "ALL") rs = rs.filter((d) => d.status === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter(
        (d) =>
          String(d.id).includes(q) ||
          lookupCustomer(d.customerId).name.toLowerCase().includes(q) ||
          d.region.toLowerCase().includes(q)
      );
    }
    const compare = (a: number | string, b: number | string) => {
      if (a < b) return sortDir === "asc" ? -1 : 1;
      if (a > b) return sortDir === "asc" ? 1 : -1;
      return 0;
    };
    return [...rs].sort((a, b) => compare(a[sortKey], b[sortKey]));
  }, [deliveries, filter, search, sortKey, sortDir]);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortKey(key);
      setSortDir("asc");
    }
  };

  const handleComplete = (deliveryId: number) => {
    const result = complete(deliveryId);
    if (result.ok) {
      toast.success(tToast("completed"));
    }
  };

  return (
    <>
      <DeliveriesFilterBar
        filter={filter}
        onFilterChange={setFilter}
        search={search}
        onSearchChange={setSearch}
        counts={counts}
      />
      <DeliveriesTableBody
        rows={filtered}
        sortKey={sortKey}
        sortDir={sortDir}
        onSortToggle={toggleSort}
        onRowClick={openDelivery}
        onComplete={handleComplete}
      />
      <DeliveryDetailSheet />
    </>
  );
}
