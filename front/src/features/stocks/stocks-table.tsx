"use client";

import { useMemo, useState } from "react";
import { useErpStore } from "@/store/erp-store";
import { lookupProduct, lookupWarehouse } from "@/lib/mock";
import {
  StocksFilterBar,
  type StockFilterKey,
} from "./stocks-filter-bar";
import {
  StocksTableBody,
  type SortDir,
  type SortKey,
} from "./stocks-table-body";
import { severityOf, type StockSeverity } from "./severity";

export function StocksTable() {
  const stocks = useErpStore((s) => s.stocks);

  const [filter, setFilter] = useState<StockFilterKey>("ALL");
  const [search, setSearch] = useState("");
  const [sortKey, setSortKey] = useState<SortKey>("available");
  const [sortDir, setSortDir] = useState<SortDir>("asc");

  const enriched = useMemo(
    () =>
      stocks.map((s) => {
        const available = s.total - s.reserved;
        return {
          ...s,
          available,
          severity: severityOf(available),
        };
      }),
    [stocks]
  );

  const counts = useMemo(() => {
    const c: Record<StockFilterKey, number> = {
      ALL: enriched.length,
      critical: 0,
      warning: 0,
      normal: 0,
    };
    for (const r of enriched) {
      c[r.severity satisfies StockSeverity]++;
    }
    return c;
  }, [enriched]);

  const filtered = useMemo(() => {
    let rs = enriched;
    if (filter !== "ALL") rs = rs.filter((r) => r.severity === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter((r) => {
        const p = lookupProduct(r.productId);
        const w = lookupWarehouse(r.warehouseId);
        return (
          p.name.toLowerCase().includes(q) ||
          p.sku.toLowerCase().includes(q) ||
          w.name.toLowerCase().includes(q)
        );
      });
    }
    const compare = (a: number | string, b: number | string) => {
      if (a < b) return sortDir === "asc" ? -1 : 1;
      if (a > b) return sortDir === "asc" ? 1 : -1;
      return 0;
    };
    return [...rs].sort((a, b) => {
      if (sortKey === "product") {
        return compare(
          lookupProduct(a.productId).name,
          lookupProduct(b.productId).name
        );
      }
      return compare(a[sortKey], b[sortKey]);
    });
  }, [enriched, filter, search, sortKey, sortDir]);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortKey(key);
      setSortDir(key === "available" ? "asc" : "desc");
    }
  };

  return (
    <>
      <StocksFilterBar
        filter={filter}
        onFilterChange={setFilter}
        search={search}
        onSearchChange={setSearch}
        counts={counts}
      />
      <StocksTableBody
        rows={filtered}
        sortKey={sortKey}
        sortDir={sortDir}
        onSortToggle={toggleSort}
      />
    </>
  );
}
