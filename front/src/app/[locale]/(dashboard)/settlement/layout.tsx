import type { ReactNode } from "react";
import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { TabsNav, type TabItem } from "@/components/ui/tabs-nav";
import {
  SETTLEMENT_PERIODS,
  LEDGERS,
  SELLER_SETTLEMENTS,
  AGING_SNAPSHOTS,
  BATCH_LOGS,
} from "@/lib/mock/settlement";

export default async function SettlementLayout({
  children,
}: {
  children: ReactNode;
}) {
  const tPage = await getTranslations("Pages.settlement");
  const tTabs = await getTranslations("Settlement.tabs");

  const items: TabItem[] = [
    { key: "periods",  href: "/settlement/periods",  label: tTabs("periods"),  count: SETTLEMENT_PERIODS.filter((p) => p.status === "OPEN").length },
    { key: "ledgers",  href: "/settlement/ledgers",  label: tTabs("ledgers"),  count: LEDGERS.length },
    { key: "sellers",  href: "/settlement/sellers",  label: tTabs("sellers"),  count: SELLER_SETTLEMENTS.filter((s) => s.status === "CALCULATED").length },
    { key: "aging",    href: "/settlement/aging",    label: tTabs("aging"),    count: AGING_SNAPSHOTS.filter((a) => a.bucket90Plus > 0).length },
    { key: "batch",    href: "/settlement/batch",    label: tTabs("batch"),    count: BATCH_LOGS.filter((b) => b.status === "FAILED").length },
  ];

  return (
    <div className="px-6 py-6">
      <PageHead title={tPage("title")} sub={tPage("subtitle")} />
      <TabsNav items={items} className="mb-5" />
      {children}
    </div>
  );
}
