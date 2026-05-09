import type { AppRole } from "@/lib/types/auth";

export const ROLES: readonly AppRole[] = [
  {
    code: "ROLE_ADMIN",
    name: "시스템 관리자",
    description: "모든 권한",
    permissionCodes: [
      "ORDER_READ", "ORDER_WRITE", "PAYMENT_PROCESS",
      "SHIPMENT_DISPATCH", "DELIVERY_COMPLETE",
      "INVENTORY_READ", "INVENTORY_WRITE", "PROCUREMENT",
      "SETTLEMENT_OPS", "HR_OPS", "APPROVAL_OPS", "AUTH_ADMIN",
    ],
  },
  {
    code: "ROLE_LOGISTICS",
    name: "물류 매니저",
    description: "출고/배송 관리",
    permissionCodes: [
      "ORDER_READ", "SHIPMENT_DISPATCH", "DELIVERY_COMPLETE",
      "INVENTORY_READ",
    ],
  },
  {
    code: "ROLE_SALES",
    name: "영업",
    description: "수주/결제/고객",
    permissionCodes: [
      "ORDER_READ", "ORDER_WRITE", "PAYMENT_PROCESS",
    ],
  },
  {
    code: "ROLE_INVENTORY",
    name: "재고 담당",
    description: "재고/입고/조정",
    permissionCodes: [
      "INVENTORY_READ", "INVENTORY_WRITE", "PROCUREMENT",
    ],
  },
  {
    code: "ROLE_FINANCE",
    name: "재무",
    description: "정산/회계",
    permissionCodes: ["SETTLEMENT_OPS"],
  },
  {
    code: "ROLE_HR",
    name: "인사",
    description: "직원/급여 관리",
    permissionCodes: ["HR_OPS", "APPROVAL_OPS"],
  },
];
