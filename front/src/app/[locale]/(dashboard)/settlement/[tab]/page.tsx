import { notFound } from "next/navigation";
import {
  SETTLEMENT_TABS,
  isSettlementTab,
} from "@/features/settlement/tab-keys";
import { PeriodsView } from "@/features/settlement/periods-view";
import { LedgersView } from "@/features/settlement/ledgers-view";
import { SellersView } from "@/features/settlement/sellers-view";
import { AgingView } from "@/features/settlement/aging-view";
import { BatchView } from "@/features/settlement/batch-view";

export function generateStaticParams() {
  return SETTLEMENT_TABS.map((tab) => ({ tab }));
}

export default async function SettlementTabPage({
  params,
}: {
  params: Promise<{ tab: string }>;
}) {
  const { tab } = await params;
  if (!isSettlementTab(tab)) notFound();

  switch (tab) {
    case "periods":
      return <PeriodsView />;
    case "ledgers":
      return <LedgersView />;
    case "sellers":
      return <SellersView />;
    case "aging":
      return <AgingView />;
    case "batch":
      return <BatchView />;
  }
}
