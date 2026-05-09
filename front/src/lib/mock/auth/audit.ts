import type { AuditLog } from "@/lib/types/auth";

export const AUDIT_LOGS: readonly AuditLog[] = [
  { id: 1, userId: 1, action: "LOGIN",            resource: "Auth",       resourceId: null,    occurredAt: "2026-05-04T08:50:00", ip: "10.0.1.21" },
  { id: 2, userId: 2, action: "ORDER_PAY",        resource: "Order",      resourceId: "2406",  occurredAt: "2026-05-04T13:42:00", ip: "10.0.1.34" },
  { id: 3, userId: 3, action: "SHIPMENT_DISPATCH", resource: "Shipment", resourceId: "7012",  occurredAt: "2026-05-04T10:08:00", ip: "10.0.1.35" },
  { id: 4, userId: 1, action: "ROLE_ASSIGN",      resource: "User",       resourceId: "5",     occurredAt: "2026-05-04T11:00:00", ip: "10.0.1.21" },
  { id: 5, userId: 1, action: "USER_LOCK",        resource: "User",       resourceId: "5",     occurredAt: "2026-04-25T11:30:00", ip: "10.0.1.21" },
  { id: 6, userId: 6, action: "PERIOD_CLOSE",     resource: "SettlementPeriod", resourceId: "2", occurredAt: "2026-05-01T00:10:00", ip: "10.0.1.40" },
  { id: 7, userId: 4, action: "QUOTE_CREATE",     resource: "Quote",      resourceId: "Q-1024", occurredAt: "2026-05-03T14:32:00", ip: "10.0.1.34" },
];
