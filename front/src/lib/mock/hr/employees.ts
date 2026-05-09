import type { Employee } from "@/lib/types/hr";

export const EMPLOYEES: readonly Employee[] = [
  { id: 1, employeeNumber: "E20240101", name: "서준",   departmentId: 1, hiredAt: "2024-01-01", baseSalary: 4_500_000, status: "ACTIVE" },
  { id: 2, employeeNumber: "E20230501", name: "이민호", departmentId: 1, hiredAt: "2023-05-01", baseSalary: 3_800_000, status: "ACTIVE" },
  { id: 3, employeeNumber: "E20220301", name: "박지훈", departmentId: 1, hiredAt: "2022-03-01", baseSalary: 3_200_000, status: "ACTIVE" },
  { id: 4, employeeNumber: "E20240601", name: "최유진", departmentId: 2, hiredAt: "2024-06-01", baseSalary: 4_100_000, status: "ACTIVE" },
  { id: 5, employeeNumber: "E20230901", name: "김지연", departmentId: 2, hiredAt: "2023-09-01", baseSalary: 3_600_000, status: "ON_LEAVE" },
  { id: 6, employeeNumber: "E20210801", name: "윤상철", departmentId: 3, hiredAt: "2021-08-01", baseSalary: 5_200_000, status: "ACTIVE" },
];
