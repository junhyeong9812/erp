export type EmployeeStatus = "ACTIVE" | "ON_LEAVE" | "TERMINATED";
export type AttendanceStatus = "NORMAL" | "LATE" | "ABSENT" | "EARLY_LEAVE";
export type LeaveStatus = "PENDING" | "APPROVED" | "REJECTED";

export interface Department {
  id: number;
  code: string;
  name: string;
}

export interface Employee {
  id: number;
  employeeNumber: string;
  name: string;
  departmentId: number;
  hiredAt: string;
  baseSalary: number;
  status: EmployeeStatus;
}

export interface Attendance {
  id: number;
  employeeId: number;
  workDate: string;
  checkInTime: string | null;
  checkOutTime: string | null;
  status: AttendanceStatus;
}

export interface LeaveRequest {
  id: number;
  employeeId: number;
  startDate: string;
  endDate: string;
  reason: string;
  status: LeaveStatus;
  requestedAt: string;
}

export interface Payroll {
  id: number;
  employeeId: number;
  year: number;
  month: number;
  baseSalary: number;
  allowance: number;
  insurance: number;
  netSalary: number;
  paidAt: string | null;
}
