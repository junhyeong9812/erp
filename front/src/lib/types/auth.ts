export type UserStatus = "ACTIVE" | "LOCKED" | "TERMINATED";

export interface AppUser {
  id: number;
  username: string;
  employeeId: number | null;
  roleCodes: string[];
  status: UserStatus;
  lastLoginAt: string | null;
  createdAt: string;
}

export interface AppRole {
  code: string;
  name: string;
  description: string;
  permissionCodes: string[];
}

export interface AppPermission {
  code: string;
  name: string;
  description: string;
}

export interface AuditLog {
  id: number;
  userId: number;
  action: string;
  resource: string;
  resourceId: string | null;
  occurredAt: string;
  ip: string;
}
