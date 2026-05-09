"use client";

import { useMemo, useState } from "react";
import { useTranslations } from "next-intl";
import { useShallow } from "zustand/react/shallow";
import { toast } from "sonner";
import { useErpStore } from "@/store/erp-store";
import { lookupCustomer } from "@/lib/mock";
import {
  OrdersFilterBar,
  type OrderFilterKey,
} from "./orders-filter-bar";
import {
  OrdersTableBody,
  type SortDir,
  type SortKey,
} from "./orders-table-body";
import { OrderDetailSheet } from "./order-detail-sheet";

export function OrdersTable() {
  const tToast = useTranslations("Orders.toast");

  const { orders, pay, openOrder } = useErpStore(
    useShallow((s) => ({
      orders: s.orders,
      pay: s.payOrder,
      openOrder: s.openOrder,
    }))
  );

  const [filter, setFilter] = useState<OrderFilterKey>("ALL");
  const [search, setSearch] = useState("");
  const [sortKey, setSortKey] = useState<SortKey>("placedAt");
  const [sortDir, setSortDir] = useState<SortDir>("desc");

  const counts = useMemo(() => {
    const c: Record<OrderFilterKey, number> = {
      ALL: orders.length,
      PLACED: 0,
      CONFIRMED: 0,
      SHIPPED: 0,
      COMPLETED: 0,
      CANCELLED: 0,
      REFUNDED: 0,
    };
    for (const o of orders) {
      c[o.status] = (c[o.status] ?? 0) + 1;
    }
    return c;
  }, [orders]);

  const filtered = useMemo(() => {
    let rs = orders;
    if (filter !== "ALL") rs = rs.filter((o) => o.status === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter(
        (o) =>
          String(o.id).includes(q) ||
          lookupCustomer(o.customerId).name.toLowerCase().includes(q)
      );
    }
    const compare = (a: number | string, b: number | string) => {
      if (a < b) return sortDir === "asc" ? -1 : 1;
      if (a > b) return sortDir === "asc" ? 1 : -1;
      return 0;
    };
    return [...rs].sort((a, b) => compare(a[sortKey], b[sortKey]));
  }, [orders, filter, search, sortKey, sortDir]);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDir("desc");
    }
  };

  const handlePay = (orderId: number) => {
    const result = pay(orderId);
    if (result.ok) {
      toast.success(tToast("paymentDone"));
    }
  };

  return (
    <>
      <OrdersFilterBar
        filter={filter}
        onFilterChange={setFilter}
        search={search}
        onSearchChange={setSearch}
        counts={counts}
      />
      <OrdersTableBody
        rows={filtered}
        sortKey={sortKey}
        sortDir={sortDir}
        onSortToggle={toggleSort}
        onRowClick={openOrder}
        onPay={handlePay}
      />
      <OrderDetailSheet />
    </>
  );
}
