"use client";

import { useMemo, useState } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { toast } from "sonner";
import { useErpStore } from "@/store/erp-store";
import { lookupCustomer } from "@/lib/mock";
import {
  ShipmentsFilterBar,
  type ShipmentFilterKey,
} from "./shipments-filter-bar";
import {
  ShipmentsTableBody,
  type SortDir,
  type SortKey,
} from "./shipments-table-body";
import { ShipmentDetailSheet } from "./shipment-detail-sheet";

export function ShipmentsTable() {
  const tToast = useTranslations("Shipments.toast");

  const { shipments, dispatch, openShipment } = useErpStore(
    useShallow((s) => ({
      shipments: s.shipments,
      dispatch: s.dispatchShipment,
      openShipment: s.openShipment,
    }))
  );

  const [filter, setFilter] = useState<ShipmentFilterKey>("ALL");
  const [search, setSearch] = useState("");
  const [sortKey, setSortKey] = useState<SortKey>("preparedAt");
  const [sortDir, setSortDir] = useState<SortDir>("desc");

  const counts = useMemo(() => {
    const c: Record<ShipmentFilterKey, number> = {
      ALL: shipments.length,
      PREPARING: 0,
      DISPATCHED: 0,
      COMPLETED: 0,
    };
    for (const s of shipments) c[s.status] = (c[s.status] ?? 0) + 1;
    return c;
  }, [shipments]);

  const filtered = useMemo(() => {
    let rs = shipments;
    if (filter !== "ALL") rs = rs.filter((s) => s.status === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter(
        (s) =>
          String(s.id).includes(q) ||
          lookupCustomer(s.customerId).name.toLowerCase().includes(q) ||
          (s.driver?.toLowerCase().includes(q) ?? false)
      );
    }
    const compare = (
      a: number | string | null | undefined,
      b: number | string | null | undefined
    ) => {
      if (a == null && b == null) return 0;
      if (a == null) return sortDir === "asc" ? 1 : -1;
      if (b == null) return sortDir === "asc" ? -1 : 1;
      if (a < b) return sortDir === "asc" ? -1 : 1;
      if (a > b) return sortDir === "asc" ? 1 : -1;
      return 0;
    };
    return [...rs].sort((a, b) => compare(a[sortKey], b[sortKey]));
  }, [shipments, filter, search, sortKey, sortDir]);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortKey(key);
      setSortDir("desc");
    }
  };

  const handleDispatch = (shipmentId: number) => {
    const result = dispatch(shipmentId);
    if (result.ok) {
      toast.success(tToast("dispatched"));
    }
  };

  return (
    <>
      <ShipmentsFilterBar
        filter={filter}
        onFilterChange={setFilter}
        search={search}
        onSearchChange={setSearch}
        counts={counts}
      />
      <ShipmentsTableBody
        rows={filtered}
        sortKey={sortKey}
        sortDir={sortDir}
        onSortToggle={toggleSort}
        onRowClick={openShipment}
        onDispatch={handleDispatch}
      />
      <ShipmentDetailSheet />
    </>
  );
}
