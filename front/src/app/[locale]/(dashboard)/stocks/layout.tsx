import type { ReactNode } from "react";
import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { TabsNav, type TabItem } from "@/components/ui/tabs-nav";
import {
  PRODUCTS,
  WAREHOUSES,
  STOCK_MOVEMENTS,
  initialStocks,
} from "@/lib/mock";
import { severityOf } from "@/features/stocks/severity";

export default async function StocksLayout({
  children,
}: {
  children: ReactNode;
}) {
  const tPage = await getTranslations("Pages.stocks");
  const tTabs = await getTranslations("Stocks.tabs");

  const criticalCount = initialStocks.filter(
    (s) => severityOf(s.total - s.reserved) === "critical"
  ).length;

  const items: TabItem[] = [
    { key: "inventory",  href: "/stocks/inventory",  label: tTabs("inventory"),  count: criticalCount },
    { key: "products",   href: "/stocks/products",   label: tTabs("products"),   count: PRODUCTS.length },
    { key: "warehouses", href: "/stocks/warehouses", label: tTabs("warehouses"), count: WAREHOUSES.length },
    { key: "movements",  href: "/stocks/movements",  label: tTabs("movements"),  count: STOCK_MOVEMENTS.length },
  ];

  return (
    <div className="px-6 py-6">
      <PageHead title={tPage("title")} sub={tPage("subtitle")} />
      <TabsNav items={items} className="mb-5" />
      {children}
    </div>
  );
}
