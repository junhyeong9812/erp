export type CustomerGrade = "VIP" | "GOLD" | "SILVER" | "NORMAL";

export interface Customer {
  id: number;
  code: string;
  name: string;
  grade: CustomerGrade;
}
