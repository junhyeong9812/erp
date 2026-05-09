import type { ReactNode } from "react";
import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { TabsNav, type TabItem } from "@/components/ui/tabs-nav";
import {
  EMPLOYEES,
  ATTENDANCES,
  LEAVE_REQUESTS,
  PAYROLLS,
} from "@/lib/mock/hr";

export default async function HrLayout({ children }: { children: ReactNode }) {
  const tPage = await getTranslations("Pages.hr");
  const tTabs = await getTranslations("Hr.tabs");

  const items: TabItem[] = [
    { key: "employees",  href: "/hr/employees",  label: tTabs("employees"),  count: EMPLOYEES.length },
    { key: "attendance", href: "/hr/attendance", label: tTabs("attendance"), count: ATTENDANCES.length },
    { key: "leaves",     href: "/hr/leaves",     label: tTabs("leaves"),     count: LEAVE_REQUESTS.filter((l) => l.status === "PENDING").length },
    { key: "payroll",    href: "/hr/payroll",    label: tTabs("payroll"),    count: PAYROLLS.length },
  ];

  return (
    <div className="px-6 py-6">
      <PageHead title={tPage("title")} sub={tPage("subtitle")} />
      <TabsNav items={items} className="mb-5" />
      {children}
    </div>
  );
}
