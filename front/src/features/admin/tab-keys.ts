export const ADMIN_TABS = [
  "users",
  "roles",
  "permissions",
  "audit",
] as const;
export type AdminTabKey = (typeof ADMIN_TABS)[number];

export function isAdminTab(value: string): value is AdminTabKey {
  return (ADMIN_TABS as readonly string[]).includes(value);
}
