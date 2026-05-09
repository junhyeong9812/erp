"use client";

import { useMemo, useState } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { toast } from "sonner";
import { useErpStore } from "@/store/erp-store";
import { lookupProduct } from "@/lib/mock";
import {
  PurchaseOrdersFilterBar,
  type PoFilterKey,
} from "./purchase-orders-filter-bar";
import {
  PurchaseOrdersTableBody,
  type SortDir,
  type SortKey,
} from "./purchase-orders-table-body";
import { PurchaseOrderDetailSheet } from "./purchase-order-detail-sheet";

export function PurchaseOrdersTable() {
  const tToast = useTranslations("Purchase.toast");

  const { purchaseOrders, receive, openPo } = useErpStore(
    useShallow((s) => ({
      purchaseOrders: s.purchaseOrders,
      receive: s.receivePurchaseOrder,
      openPo: s.openPurchaseOrder,
    }))
  );

  const [filter, setFilter] = useState<PoFilterKey>("ALL");
  const [search, setSearch] = useState("");
  const [sortKey, setSortKey] = useState<SortKey>("issuedAt");
  const [sortDir, setSortDir] = useState<SortDir>("desc");

  const counts = useMemo(() => {
    const c: Record<PoFilterKey, number> = {
      ALL: purchaseOrders.length,
      ISSUED: 0,
      PARTIAL: 0,
      COMPLETED: 0,
      CANCELLED: 0,
    };
    for (const p of purchaseOrders) c[p.status] = (c[p.status] ?? 0) + 1;
    return c;
  }, [purchaseOrders]);

  const filtered = useMemo(() => {
    let rs = purchaseOrders;
    if (filter !== "ALL") rs = rs.filter((p) => p.status === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter(
        (p) =>
          String(p.id).includes(q) ||
          p.supplier.toLowerCase().includes(q) ||
          lookupProduct(p.productId).name.toLowerCase().includes(q) ||
          lookupProduct(p.productId).sku.toLowerCase().includes(q)
      );
    }
    const compare = (a: number | string, b: number | string) => {
      if (a < b) return sortDir === "asc" ? -1 : 1;
      if (a > b) return sortDir === "asc" ? 1 : -1;
      return 0;
    };
    return [...rs].sort((a, b) => compare(a[sortKey], b[sortKey]));
  }, [purchaseOrders, filter, search, sortKey, sortDir]);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortKey(key);
      setSortDir("desc");
    }
  };

  const handleReceive = (poId: number) => {
    const result = receive(poId);
    if (result.ok) {
      toast.success(tToast("received"));
    }
  };

  return (
    <>
      <PurchaseOrdersFilterBar
        filter={filter}
        onFilterChange={setFilter}
        search={search}
        onSearchChange={setSearch}
        counts={counts}
      />
      <PurchaseOrdersTableBody
        rows={filtered}
        sortKey={sortKey}
        sortDir={sortDir}
        onSortToggle={toggleSort}
        onRowClick={openPo}
        onReceive={handleReceive}
      />
      <PurchaseOrderDetailSheet />
    </>
  );
}
