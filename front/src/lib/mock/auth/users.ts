import type { AppUser } from "@/lib/types/auth";

export const APP_USERS: readonly AppUser[] = [
  { id: 1, username: "admin",      employeeId: 6, roleCodes: ["ROLE_ADMIN"],                       status: "ACTIVE", lastLoginAt: "2026-05-04T08:50:00", createdAt: "2024-01-01T00:00:00" },
  { id: 2, username: "seojun",     employeeId: 1, roleCodes: ["ROLE_LOGISTICS"],                   status: "ACTIVE", lastLoginAt: "2026-05-04T08:52:00", createdAt: "2024-01-01T00:00:00" },
  { id: 3, username: "minho",      employeeId: 2, roleCodes: ["ROLE_LOGISTICS"],                   status: "ACTIVE", lastLoginAt: "2026-05-04T09:14:00", createdAt: "2023-05-01T00:00:00" },
  { id: 4, username: "yujin.choi", employeeId: 4, roleCodes: ["ROLE_SALES"],                       status: "ACTIVE", lastLoginAt: "2026-05-04T09:08:00", createdAt: "2024-06-01T00:00:00" },
  { id: 5, username: "jiyeon.kim", employeeId: 5, roleCodes: ["ROLE_SALES"],                       status: "LOCKED", lastLoginAt: "2026-04-25T11:00:00", createdAt: "2023-09-01T00:00:00" },
  { id: 6, username: "scyoon",     employeeId: 6, roleCodes: ["ROLE_FINANCE", "ROLE_HR"],          status: "ACTIVE", lastLoginAt: "2026-05-04T08:30:00", createdAt: "2021-08-01T00:00:00" },
];
