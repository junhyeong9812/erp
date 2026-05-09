export const CRM_TABS = [
  "customers",
  "consultations",
  "claims",
  "points",
  "coupons",
] as const;
export type CrmTabKey = (typeof CRM_TABS)[number];

export function isCrmTab(value: string): value is CrmTabKey {
  return (CRM_TABS as readonly string[]).includes(value);
}
