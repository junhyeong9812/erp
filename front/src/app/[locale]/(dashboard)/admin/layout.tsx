import type { ReactNode } from "react";
import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { TabsNav, type TabItem } from "@/components/ui/tabs-nav";
import {
  APP_USERS,
  ROLES,
  PERMISSIONS,
  AUDIT_LOGS,
} from "@/lib/mock/auth";

export default async function AdminLayout({
  children,
}: {
  children: ReactNode;
}) {
  const tPage = await getTranslations("Pages.admin");
  const tTabs = await getTranslations("Admin.tabs");

  const items: TabItem[] = [
    { key: "users",       href: "/admin/users",       label: tTabs("users"),       count: APP_USERS.filter((u) => u.status === "LOCKED").length },
    { key: "roles",       href: "/admin/roles",       label: tTabs("roles"),       count: ROLES.length },
    { key: "permissions", href: "/admin/permissions", label: tTabs("permissions"), count: PERMISSIONS.length },
    { key: "audit",       href: "/admin/audit",       label: tTabs("audit"),       count: AUDIT_LOGS.length },
  ];

  return (
    <div className="px-6 py-6">
      <PageHead title={tPage("title")} sub={tPage("subtitle")} />
      <TabsNav items={items} className="mb-5" />
      {children}
    </div>
  );
}
