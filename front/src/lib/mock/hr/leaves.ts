import type { LeaveRequest } from "@/lib/types/hr";

export const LEAVE_REQUESTS: readonly LeaveRequest[] = [
  {
    id: 1,
    employeeId: 4,
    startDate: "2026-05-10",
    endDate: "2026-05-12",
    reason: "개인 사유 (가족 여행)",
    status: "PENDING",
    requestedAt: "2026-05-03T14:32:00",
  },
  {
    id: 2,
    employeeId: 5,
    startDate: "2026-05-04",
    endDate: "2026-05-08",
    reason: "병가",
    status: "APPROVED",
    requestedAt: "2026-05-02T09:15:00",
  },
  {
    id: 3,
    employeeId: 2,
    startDate: "2026-04-22",
    endDate: "2026-04-23",
    reason: "여름 휴가 (조기 사용)",
    status: "REJECTED",
    requestedAt: "2026-04-15T16:00:00",
  },
];
