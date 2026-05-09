import { notFound } from "next/navigation";
import { ADMIN_TABS, isAdminTab } from "@/features/admin/tab-keys";
import { UsersView } from "@/features/admin/users-view";
import { RolesView } from "@/features/admin/roles-view";
import { PermissionsView } from "@/features/admin/permissions-view";
import { AuditView } from "@/features/admin/audit-view";

export function generateStaticParams() {
  return ADMIN_TABS.map((tab) => ({ tab }));
}

export default async function AdminTabPage({
  params,
}: {
  params: Promise<{ tab: string }>;
}) {
  const { tab } = await params;
  if (!isAdminTab(tab)) notFound();

  switch (tab) {
    case "users":
      return <UsersView />;
    case "roles":
      return <RolesView />;
    case "permissions":
      return <PermissionsView />;
    case "audit":
      return <AuditView />;
  }
}
