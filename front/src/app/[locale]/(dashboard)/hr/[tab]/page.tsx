import { notFound } from "next/navigation";
import { HR_TABS, isHrTab } from "@/features/hr/tab-keys";
import { EmployeesView } from "@/features/hr/employees-view";
import { AttendanceView } from "@/features/hr/attendance-view";
import { LeavesView } from "@/features/hr/leaves-view";
import { PayrollView } from "@/features/hr/payroll-view";

export function generateStaticParams() {
  return HR_TABS.map((tab) => ({ tab }));
}

export default async function HrTabPage({
  params,
}: {
  params: Promise<{ tab: string }>;
}) {
  const { tab } = await params;
  if (!isHrTab(tab)) notFound();

  switch (tab) {
    case "employees":
      return <EmployeesView />;
    case "attendance":
      return <AttendanceView />;
    case "leaves":
      return <LeavesView />;
    case "payroll":
      return <PayrollView />;
  }
}
