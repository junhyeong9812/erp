import type { Department, Employee } from "@/lib/types/hr";
import { DEPARTMENTS } from "./departments";
import { EMPLOYEES } from "./employees";

export { DEPARTMENTS } from "./departments";
export { EMPLOYEES } from "./employees";
export { ATTENDANCES } from "./attendances";
export { LEAVE_REQUESTS } from "./leaves";
export { PAYROLLS } from "./payrolls";

const PLACEHOLDER_DEPARTMENT: Department = {
  id: 0,
  code: "—",
  name: "—",
};

const PLACEHOLDER_EMPLOYEE: Employee = {
  id: 0,
  employeeNumber: "—",
  name: "—",
  departmentId: 0,
  hiredAt: "—",
  baseSalary: 0,
  status: "TERMINATED",
};

export function lookupDepartment(id: number): Department {
  return DEPARTMENTS.find((d) => d.id === id) ?? PLACEHOLDER_DEPARTMENT;
}

export function lookupEmployee(id: number): Employee {
  return EMPLOYEES.find((e) => e.id === id) ?? PLACEHOLDER_EMPLOYEE;
}
