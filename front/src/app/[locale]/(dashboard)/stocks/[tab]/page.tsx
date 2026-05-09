import { notFound } from "next/navigation";
import { STOCKS_TABS, isStocksTab } from "@/features/stocks/tab-keys";
import { StocksTable } from "@/features/stocks/stocks-table";
import { ProductsView } from "@/features/stocks/products-view";
import { WarehousesView } from "@/features/stocks/warehouses-view";
import { MovementsView } from "@/features/stocks/movements-view";

export function generateStaticParams() {
  return STOCKS_TABS.map((tab) => ({ tab }));
}

export default async function StocksTabPage({
  params,
}: {
  params: Promise<{ tab: string }>;
}) {
  const { tab } = await params;
  if (!isStocksTab(tab)) notFound();

  switch (tab) {
    case "inventory":
      return <StocksTable />;
    case "products":
      return <ProductsView />;
    case "warehouses":
      return <WarehousesView />;
    case "movements":
      return <MovementsView />;
  }
}
