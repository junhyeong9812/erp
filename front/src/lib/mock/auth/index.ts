import type { AppUser } from "@/lib/types/auth";
import { APP_USERS } from "./users";

export { APP_USERS } from "./users";
export { ROLES } from "./roles";
export { PERMISSIONS } from "./permissions";
export { AUDIT_LOGS } from "./audit";

const PLACEHOLDER_USER: AppUser = {
  id: 0,
  username: "—",
  employeeId: null,
  roleCodes: [],
  status: "TERMINATED",
  lastLoginAt: null,
  createdAt: "—",
};

export function lookupAppUser(id: number): AppUser {
  return APP_USERS.find((u) => u.id === id) ?? PLACEHOLDER_USER;
}

export const DEFAULT_CURRENT_USER_ID = 1;
