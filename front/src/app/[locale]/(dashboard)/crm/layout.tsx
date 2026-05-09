import type { ReactNode } from "react";
import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { TabsNav, type TabItem } from "@/components/ui/tabs-nav";
import { CUSTOMERS } from "@/lib/mock";
import { CONSULTATIONS, CLAIMS, POINTS, COUPONS } from "@/lib/mock/crm";

export default async function CrmLayout({ children }: { children: ReactNode }) {
  const tPage = await getTranslations("Pages.crm");
  const tTabs = await getTranslations("Crm.tabs");

  const items: TabItem[] = [
    { key: "customers",     href: "/crm/customers",     label: tTabs("customers"),     count: CUSTOMERS.length },
    { key: "consultations", href: "/crm/consultations", label: tTabs("consultations"), count: CONSULTATIONS.filter((c) => c.status === "OPEN").length },
    { key: "claims",        href: "/crm/claims",        label: tTabs("claims"),        count: CLAIMS.filter((c) => c.status === "OPEN" || c.status === "IN_PROGRESS").length },
    { key: "points",        href: "/crm/points",        label: tTabs("points"),        count: POINTS.filter((p) => p.status === "ACTIVE").length },
    { key: "coupons",       href: "/crm/coupons",       label: tTabs("coupons"),       count: COUPONS.filter((c) => c.status === "ISSUED").length },
  ];

  return (
    <div className="px-6 py-6">
      <PageHead title={tPage("title")} sub={tPage("subtitle")} />
      <TabsNav items={items} className="mb-5" />
      {children}
    </div>
  );
}
