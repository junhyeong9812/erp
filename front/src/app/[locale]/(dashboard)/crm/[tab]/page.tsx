import { notFound } from "next/navigation";
import { CRM_TABS, isCrmTab } from "@/features/crm/tab-keys";
import { CustomersView } from "@/features/crm/customers-view";
import { ConsultationsView } from "@/features/crm/consultations-view";
import { ClaimsView } from "@/features/crm/claims-view";
import { PointsView } from "@/features/crm/points-view";
import { CouponsView } from "@/features/crm/coupons-view";

export function generateStaticParams() {
  return CRM_TABS.map((tab) => ({ tab }));
}

export default async function CrmTabPage({
  params,
}: {
  params: Promise<{ tab: string }>;
}) {
  const { tab } = await params;
  if (!isCrmTab(tab)) notFound();

  switch (tab) {
    case "customers":
      return <CustomersView />;
    case "consultations":
      return <ConsultationsView />;
    case "claims":
      return <ClaimsView />;
    case "points":
      return <PointsView />;
    case "coupons":
      return <CouponsView />;
  }
}
