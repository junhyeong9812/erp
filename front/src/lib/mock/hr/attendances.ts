import type { Attendance } from "@/lib/types/hr";

const TODAY = "2026-05-04";

export const ATTENDANCES: readonly Attendance[] = [
  { id: 1, employeeId: 1, workDate: TODAY, checkInTime: "08:52", checkOutTime: null,    status: "NORMAL" },
  { id: 2, employeeId: 2, workDate: TODAY, checkInTime: "09:14", checkOutTime: null,    status: "LATE"   },
  { id: 3, employeeId: 3, workDate: TODAY, checkInTime: "08:48", checkOutTime: null,    status: "NORMAL" },
  { id: 4, employeeId: 4, workDate: TODAY, checkInTime: "09:08", checkOutTime: null,    status: "LATE"   },
  { id: 5, employeeId: 5, workDate: TODAY, checkInTime: null,    checkOutTime: null,    status: "ABSENT" },
  { id: 6, employeeId: 6, workDate: TODAY, checkInTime: "08:30", checkOutTime: "17:35", status: "NORMAL" },
];
