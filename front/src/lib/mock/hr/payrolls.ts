import type { Payroll } from "@/lib/types/hr";

const calc = (base: number, allowance: number) => {
  const insurance = Math.round((base + allowance) * 0.0945);
  return { insurance, netSalary: base + allowance - insurance };
};

const r1 = calc(4_500_000, 500_000);
const r2 = calc(3_800_000, 400_000);
const r3 = calc(3_200_000, 300_000);
const r4 = calc(4_100_000, 450_000);
const r6 = calc(5_200_000, 600_000);

export const PAYROLLS: readonly Payroll[] = [
  { id: 1, employeeId: 1, year: 2026, month: 4, baseSalary: 4_500_000, allowance: 500_000, ...r1, paidAt: "2026-04-25T00:00:00" },
  { id: 2, employeeId: 2, year: 2026, month: 4, baseSalary: 3_800_000, allowance: 400_000, ...r2, paidAt: "2026-04-25T00:00:00" },
  { id: 3, employeeId: 3, year: 2026, month: 4, baseSalary: 3_200_000, allowance: 300_000, ...r3, paidAt: "2026-04-25T00:00:00" },
  { id: 4, employeeId: 4, year: 2026, month: 4, baseSalary: 4_100_000, allowance: 450_000, ...r4, paidAt: "2026-04-25T00:00:00" },
  { id: 5, employeeId: 6, year: 2026, month: 4, baseSalary: 5_200_000, allowance: 600_000, ...r6, paidAt: "2026-04-25T00:00:00" },
];
