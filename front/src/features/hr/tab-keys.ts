export const HR_TABS = ["employees", "attendance", "leaves", "payroll"] as const;
export type HrTabKey = (typeof HR_TABS)[number];

export function isHrTab(value: string): value is HrTabKey {
  return (HR_TABS as readonly string[]).includes(value);
}
